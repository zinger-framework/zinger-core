class Admin::ShopController < AdminController
  before_action :load_shop, except: :new

  def new
    shop = Employee.current.shops.where(status: Shop::PENDING_STATUSES).first
    shop = Employee.current.shops.create(category: Shop::CATEGORIES['OTHERS']) if shop.nil?
    render status: 200, json: { success: true, message: 'success', data: { shop: shop.as_json('admin_shop') } }
  end

  def show
    render status: 200, json: { success: true, message: 'success', data: { shop: @shop.as_json('admin_shop') } }
  end

  def update
    shop_detail = @shop.shop_detail

    if Shop::PENDING_STATUSES.include? @shop.status
      missing_keys = %w(name description tags category street area city state pincode lat lng mobile email opening_time 
        closing_time account_number account_ifsc account_holder pan) - params.keys.select { |key| params[key].present? }
      reason = missing_keys.inject({}) { |resp, key| resp[key] = [I18n.t('validation.required', param: key.humanize)]; resp }
      reason['icon'] = [I18n.t('shop.icon.invalid_file')] if @shop.icon.blank?
      reason['cover_photos'] = [I18n.t('shop.cover_photo.invalid_file')] if shop_detail.cover_photos.blank?
      if reason.present?
        render status: 400, json: { success: false, message: I18n.t('shop.update_failed'), reason: reason }
        return
      end
    end

    %w(name email).each { |key| @shop.send("#{key}=", params[key].to_s.strip) if params[key].present? }
    @shop.tags = params['tags'].map{ |tag| tag.parameterize(separator: '_').upcase }.join(' ') if params['tags'].present?
    @shop.category = Shop::CATEGORIES[params['category'].to_s.strip.upcase]
    @shop.lat, @shop.lng = params['lat'].to_f, params['lng'].to_f if params['lat'].present? && params['lng'].present?

    @shop.validate
    if @shop.errors.any?
      render status: 400, json: { success: false, message: I18n.t('shop.update_failed'), reason: @shop.errors }
      return
    end

    shop_detail.payment = shop_detail.payment.merge(params.as_json.slice(*%w(account_number account_ifsc account_holder pan gst))
      .transform_values { |v| v.to_s.strip }.select { |key| params[key].present? })
    shop_detail.address = shop_detail.address.merge(params.as_json.slice(*%w(street area city state pincode))
      .transform_values { |v| v.to_s.strip }.select { |key| params[key].present? })
    %w(telephone mobile description).each { |key| shop_detail.send("#{key}=", params[key].to_s.strip) if params[key].present? }
    %w(opening_time closing_time).each { |key| shop_detail.send("#{key}=", 
      Time.find_zone(PlatformConfig['time_zone']).strptime(params[key], '%H:%M').utc) if params[key].present? }

    shop_detail.validate
    if shop_detail.errors.any?
      render status: 400, json: { success: false, message: I18n.t('shop.update_failed'), reason: shop_detail.errors }
      return
    end

    shop_detail.save!(validate: false)
    @shop.save!(validate: false)
    render status: 200, json: { success: true, message: I18n.t('shop.update_success'), data: { shop: @shop.as_json('admin_shop') } }
  end

  def destroy
    @shop.update!(deleted: true)
    render status: 200, json: { success: true, message: I18n.t('shop.delete_success') }
  end

  def icon
    begin
      raise I18n.t('shop.icon.already_exist') if @shop.icon.present?
      resp = validate_image_file 'icon', params['icon_file'], '512x512'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('shop.icon.upload_failed'), reason: { icon: [e.message] } }
      return
    end

    @shop.update!(icon: "#{Time.now.to_i}-#{params['icon_file'].original_filename}")
    File.open(params['icon_file'].path, 'rb') { |file| Core::Storage.upload_file(@shop.icon_key_path, file) }
    render status: 200, json: { success: true, message: I18n.t('shop.icon.upload_success'), data: { icon: Core::Storage.fetch_url(@shop.icon_key_path) } }
  end

  def cover_photo
    shop_detail = @shop.shop_detail
    cover_photos = shop_detail.cover_photos.to_a
    
    begin
      # TODO: Move limit to shop-level config - Logesh
      raise I18n.t('shop.cover_photo.limit_exceeded', limit: cover_photos.length, platform: PlatformConfig['name']) if cover_photos.length >= 10
      resp = validate_image_file 'cover_photo', params['cover_file'], '1024x500'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { success: false, message: I18n.t('shop.cover_photo.upload_failed'), reason: { cover_photos: [e.message] } }
      return
    end

    cover_photo = "#{Time.now.to_i}-#{params['cover_file'].original_filename}"
    cover_photos << cover_photo
    shop_detail.update!(cover_photos: cover_photos)
    File.open(params['cover_file'].path, 'rb') { |file| Core::Storage.upload_file(shop_detail.cover_photo_key_path(cover_photo), file) }
    render status: 200, json: { success: true, message: I18n.t('shop.cover_photo.upload_success'), 
      data: { cover_photos: cover_photos.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
        'url' => Core::Storage.fetch_url(shop_detail.cover_photo_key_path(cover_photo)) } } } }
  end

  def delete_icon
    if @shop.icon.blank?
      render status: 404, json: { success: false, message: I18n.t('shop.icon.not_found') }
      return
    end

    Core::Storage.delete_file(@shop.icon_key_path)
    @shop.update!(icon: nil)
    render status: 200, json: { success: true, message: I18n.t('shop.icon.delete_success') }
  end

  def delete_cover_photo
    shop_detail = @shop.shop_detail
    cover_photos = shop_detail.cover_photos.to_a
    cover_photo = cover_photos.find { |cover_photo| cover_photo.split('-')[0] == params['cover_photo_id'].to_s }
    if cover_photo.nil?
      render status: 404, json: { success: false, message: I18n.t('shop.cover_photo.not_found') }
      return
    end
    
    Core::Storage.delete_file(shop_detail.cover_photo_key_path(cover_photo))
    cover_photos.delete(cover_photo)
    shop_detail.update!(cover_photos: cover_photos)
    render status: 200, json: { success: true, message: I18n.t('shop.cover_photo.delete_success'), 
      data: { cover_photos: cover_photos.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
        'url' => Core::Storage.fetch_url(shop_detail.cover_photo_key_path(cover_photo)) } } } }
  end

  private

  def load_shop
    @shop = Shop.fetch_by_id(params['id'])
    if @shop.nil?
      render status: 400, json: { success: false, message: I18n.t('shop.not_found') }
      return
    end
  end

  def validate_image_file purpose, image_file, dimension
    return I18n.t("shop.#{purpose}.invalid_file") if image_file.class != ActionDispatch::Http::UploadedFile ||
      !%w(jpg jpeg png).include?(File.extname(image_file.path)[1..-1]) || `identify -format '%wx%h' #{image_file.path}` != dimension
    return I18n.t("shop.#{purpose}.file_size_exceeded") if (File.size(image_file.path).to_i / 1000) > 1024
    return I18n.t('validation.invalid', param: 'file name') if image_file.original_filename.split('.')[0].match(/^[a-zA-Z0-9\-_]{1,100}$/).nil?
    return true
  end
end
