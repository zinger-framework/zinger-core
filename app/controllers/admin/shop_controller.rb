class Admin::ShopController < AdminController
  before_action :load_shop, except: [:index, :new]

  def index
    params['page_size'] ||= LIMIT
    filter_params = params['next_page_token'].present? ? JSON.parse(Base64.decode64(params['next_page_token'])) : params
    filter_params = filter_params.slice(*%w(start_date end_date statuses id sort_order page_size next_id))

    query = ValidateParam::Shop.load_conditions(filter_params)
    if query.class == Hash
      render status: 400, json: { success: false, message: I18n.t('shop.fetch_failed'), reason: query }
      return
    end

    query, data = query.preload(:shop_detail), { shops: [] }
    data = data.merge({ total: query.count, page_size: params['page_size'].to_i }) if params['next_page_token'].blank?
    
    if params['next_page_token'].present? || data[:total] > 0
      data[:shops] = query.limit(filter_params['page_size'].to_i + 1).as_json('admin_shop_list')
      if data[:shops].size > filter_params['page_size'].to_i
        filter_params['next_id'] = data[:shops].pop['id']
        data[:next_page_token] = Base64.encode64(filter_params.to_json).gsub(/[^0-9a-z]/i, '')
      end
    end

    render status: 200, json: { success: true, message: 'success', data: data }
  end

  def new
    shop = AdminUser.current.shops.undeleted.where(status: Shop::PENDING_STATUSES).first
    shop = AdminUser.current.shops.create(category: Shop::CATEGORIES['OTHERS']) if shop.nil?
    render status: 200, json: { success: true, message: 'success', data: { shop: shop.as_json('admin_shop') } }
  end

  def show
    render status: 200, json: { success: true, message: 'success', data: { shop: @shop.as_json('admin_shop') } }
  end

  def update
    shop_detail, reason, err_key = @shop.shop_detail, {}, 'update'

    if @shop.status == Shop::STATUSES['DRAFT']
      err_key = 'create'
      missing_keys = %w(name description tags category street area city state pincode lat lng mobile email opening_time 
        closing_time account_number account_ifsc account_holder pan) - params.keys.select { |key| params[key].present? }
      reason = missing_keys.inject({}) { |resp, key| resp[key] = [I18n.t('validation.required', param: key.humanize)]; resp }
      reason['icon'] = [I18n.t('validation.icon.invalid_file')] if @shop.icon.blank?
      reason['cover_photos'] = [I18n.t('validation.cover_photo.invalid_file')] if shop_detail.cover_photos.blank?
      if reason.present?
        render status: 400, json: { success: false, message: I18n.t("shop.#{err_key}_failed"), reason: reason }
        return
      end
    end

    %w(name email).each { |key| @shop.send("#{key}=", params[key].to_s.strip) if params[key].present? }
    @shop.tags = params['tags'].map{ |tag| tag.parameterize(separator: '_').upcase }.join(' ') if params['tags'].present?
    @shop.category = Shop::CATEGORIES[params['category'].to_s.strip.upcase] if params['category'].present?
    @shop.lat, @shop.lng = params['lat'].to_f, params['lng'].to_f if params['lat'].present? && params['lng'].present?
    if params['status'].present?
      if !%w(ACTIVE INACTIVE).include?(params['status']) || (params['status'] == 'ACTIVE' && @shop.status_was != Shop::STATUSES['INACTIVE'])
        reason = reason.merge({ status: [I18n.t('validation.invalid', param: 'status')] })
      else
        @shop.status = Shop::STATUSES[params['status'].to_s.strip.upcase]
      end
    end
    @shop.validate
    reason = reason.merge(@shop.errors) if @shop.errors.any?

    shop_detail.payment = shop_detail.payment.merge(params.as_json.slice(*%w(account_number account_ifsc account_holder pan gst))
      .transform_values { |v| v.to_s.strip }.select { |key| params[key].present? })
    shop_detail.address = shop_detail.address.merge(params.as_json.slice(*%w(street area city state pincode))
      .transform_values { |v| v.to_s.strip }.select { |key| params[key].present? })
    %w(telephone mobile description).each { |key| shop_detail.send("#{key}=", params[key].to_s.strip) if params[key].present? }
    %w(opening_time closing_time).each { |key| shop_detail.send("#{key}=", 
      Time.find_zone(PlatformConfig['time_zone']).strptime(params[key], '%H:%M').utc) if params[key].present? }
    shop_detail.validate
    reason = reason.merge(shop_detail.errors) if shop_detail.errors.any?

    if reason.present?
      render status: 400, json: { success: false, message: I18n.t("shop.#{err_key}_failed"), reason: reason }
      return
    end

    @shop.status = Shop::STATUSES['PENDING'] if [Shop::STATUSES['DRAFT'], Shop::STATUSES['REJECTED']].include?(@shop.status_was)
    shop_detail.save!(validate: false)
    @shop.save!(validate: false)
    render status: 200, json: { success: true, message: I18n.t("shop.#{err_key}_success"), data: { shop: @shop.as_json('admin_shop') } }
  end

  def icon
    begin
      raise I18n.t('validation.icon.already_exist') if @shop.icon.present?
      resp = validate_image_file 'icon', params['icon_file'], '512x512'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('validation.icon.upload_failed'), reason: { icon: [e.message] } }
      return
    end

    @shop.update!(icon: "#{Time.now.to_i}-#{params['icon_file'].original_filename}")
    File.open(params['icon_file'].path, 'rb') { |file| Core::Storage.upload_file(@shop.icon_key_path, file) }
    render status: 200, json: { success: true, message: I18n.t('validation.icon.upload_success'), data: { icon: Core::Storage.fetch_url(@shop.icon_key_path) } }
  end

  def cover_photo
    shop_detail = @shop.shop_detail
    cover_photos = shop_detail.cover_photos.to_a
    
    begin
      # TODO: Move limit to shop-level config - Logesh
      raise I18n.t('validation.cover_photo.limit_exceeded', limit: cover_photos.length, platform: PlatformConfig['name']) if cover_photos.length >= 10
      resp = validate_image_file 'cover_photo', params['cover_file'], '1024x500'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('validation.cover_photo.upload_failed'), reason: { cover_photos: [e.message] } }
      return
    end

    cover_photo = "#{Time.now.to_i}-#{params['cover_file'].original_filename}"
    cover_photos << cover_photo
    shop_detail.update!(cover_photos: cover_photos)
    File.open(params['cover_file'].path, 'rb') { |file| Core::Storage.upload_file(shop_detail.cover_photo_key_path(cover_photo), file) }
    render status: 200, json: { success: true, message: I18n.t('validation.cover_photo.upload_success'), 
      data: { cover_photos: cover_photos.map { |cover_photo| { id: cover_photo.split('-')[0].to_i, 
        url: Core::Storage.fetch_url(shop_detail.cover_photo_key_path(cover_photo)) } } } }
  end

  def delete_icon
    if @shop.icon.blank?
      render status: 404, json: { success: false, message: I18n.t('validation.icon.delete_failed'), reason: { 
        icon: [I18n.t('validation.icon.not_found')] } }
      return
    end

    Core::Storage.delete_file(@shop.icon_key_path)
    @shop.update!(icon: nil)
    render status: 200, json: { success: true, message: I18n.t('validation.icon.delete_success') }
  end

  def delete_cover_photo
    shop_detail = @shop.shop_detail
    cover_photos = shop_detail.cover_photos.to_a
    cover_photo = cover_photos.find { |cover_photo| cover_photo.split('-')[0] == params['cover_photo_id'].to_s }
    if cover_photo.nil?
      render status: 404, json: { success: false, message: I18n.t('validation.cover_photo.delete_failed'), reason: {
        cover_photos: [I18n.t('validation.cover_photo.not_found')] } }
      return
    end
    
    Core::Storage.delete_file(shop_detail.cover_photo_key_path(cover_photo))
    cover_photos.delete(cover_photo)
    shop_detail.update!(cover_photos: cover_photos)
    render status: 200, json: { success: true, message: I18n.t('validation.cover_photo.delete_success'), 
      data: { cover_photos: cover_photos.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
        'url' => Core::Storage.fetch_url(shop_detail.cover_photo_key_path(cover_photo)) } } } }
  end

  private

  def load_shop
    @shop = Shop.fetch_by_id(params['id'])
    if @shop.nil?
      render status: 404, json: { success: false, message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    elsif params['action'] != 'show' && @shop.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('validation.invalid_request'), 
        reason: I18n.t('shop.blocked', platform: PlatformConfig['name']) }
      return
    end
  end
end
