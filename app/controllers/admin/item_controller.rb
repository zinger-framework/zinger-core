class Admin::ItemController < AdminController
  before_action :load_shop, except: :meta
  before_action :load_item, except: [:index, :create, :meta]

  def index
    params['page_size'] ||= LIMIT
    filter_params = params['next_page_token'].present? ? JSON.parse(Base64.decode64(params['next_page_token'])) : params
    filter_params = filter_params.slice(*%w(item_type categories include_inactive sort_order page_size next_id)).merge({ 'id' => ShortUUID.expand(params['id']) })

    query = ValidateParam::Item.load_conditions(filter_params, { 'shop' => @shop })
    if query.class == Hash
      render status: 400, json: { message: I18n.t('item.fetch_failed'), reason: query }
      return
    end

    query, data = query, { items: [] }
    data = data.merge({ total: query.count, page_size: params['page_size'].to_i }) if params['next_page_token'].blank?
    
    if params['next_page_token'].present? || data[:total] > 0
      options = fetch_options
      data[:items] = query.limit(filter_params['page_size'].to_i + 1).map { |item| item.as_json('admin_item_list', options) }
      if data[:items].size > filter_params['page_size'].to_i
        filter_params['next_id'] = ShortUUID.expand(data[:items].pop['id'])
        data[:next_page_token] = Base64.encode64(filter_params.to_json).gsub(/[^0-9a-z]/i, '')
      end
    end

    render status: 200, json: { message: 'success', data: data }
  end

  def create
    item = @shop.items.create(name: params['name'], description: params['description'], category: params['category'], 
      item_type: params['item_type'], meta: params.permit(meta_data: {}, filterable_fields: {}))
    if item.errors.any?
      render status: 400, json: { message: I18n.t('item.create_failed'), reason: item.errors.messages }
      return
    end

    render status: 200, json: { message: 'success', data: { item: item.as_json('admin_item', fetch_options({ 'item_type' => item.item_type })) } }
  end

  def show
    render status: 200, json: { message: 'success', data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def update
    %w(name description category status).each { |key| @item.send("#{key}=", params[key].to_s.strip) if params[key].present? }    
    @item.meta.merge!(params.permit(meta_data: {}, filterable_fields: {})) if params.slice(:meta_data, :filterable_fields).present?

    @item.validate
    if @item.errors.any?
      render status: 400, json: { message: I18n.t('item.update_failed'), reason: @item.errors }
      return
    end

    @item.save!(validate: false)
    render status: 200, json: { message: I18n.t('item.update_success'), data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def icon
    begin
      raise I18n.t('validation.icon.already_exist') if @item.icon.present?
      resp = validate_image_file 'icon', params['icon_file'], '512x512'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { message: I18n.t('validation.icon.upload_failed'), reason: { icon: [e.message] } }
      return
    end

    @item.update!(icon: "#{Time.now.to_i}-#{params['icon_file'].original_filename}")
    File.open(params['icon_file'].path, 'rb') { |file| Core::Storage.upload_file(@item.icon_key_path, file) }
    render status: 200, json: { message: I18n.t('validation.icon.upload_success'), data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def cover_photo
    cover_photos = @item.cover_photos.to_a
    
    begin
      raise I18n.t('validation.cover_photo.limit_exceeded', limit: cover_photos.length, platform: PlatformConfig['name']) if cover_photos.length >= Item::LIMITS[:cover_photos]
      resp = validate_image_file 'cover_photo', params['cover_file'], '1024x500'
      raise resp if resp.class == String
    rescue => e
      render status: 400, json: { message: I18n.t('validation.cover_photo.upload_failed'), reason: { cover_photos: [e.message] } }
      return
    end

    cover_photo = "#{Time.now.to_i}-#{params['cover_file'].original_filename}"
    cover_photos << cover_photo
    @item.update!(cover_photos: cover_photos)
    File.open(params['cover_file'].path, 'rb') { |file| Core::Storage.upload_file(@item.cover_photo_key_path(cover_photo), file) }
    render status: 200, json: { message: I18n.t('validation.cover_photo.upload_success'), 
      data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def variant
    variant = @item.item_variants.create(variant_name: params['variant_name'], variant_value: params['variant_value'], 
      item_type: @item.item_type, actual_price: params['variant_price'].to_f)
    
    if variant.errors.any?
      render status: 400, json: { message: I18n.t('item.variant.create_failed'), reason: variant.errors }
      return
    end

    render status: 200, json: { message: I18n.t('item.variant.create_success'), data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def delete_icon
    if @item.icon.blank?
      render status: 404, json: { message: I18n.t('validation.icon.delete_failed'), reason: { 
        icon: [I18n.t('validation.icon.not_found')] } }
      return
    end

    Core::Storage.delete_file(@item.icon_key_path)
    @item.update!(icon: nil)
    render status: 200, json: { message: I18n.t('validation.icon.delete_success'), data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def delete_cover_photo
    cover_photos = @item.cover_photos.to_a
    cover_photo = cover_photos.find { |cover_photo| cover_photo.split('-')[0] == params['cover_photo_id'].to_s }
    if cover_photo.nil?
      render status: 404, json: { message: I18n.t('validation.cover_photo.delete_failed'), reason: {
        cover_photos: [I18n.t('validation.cover_photo.not_found')] } }
      return
    end
    
    Core::Storage.delete_file(@item.cover_photo_key_path(cover_photo))
    cover_photos.delete(cover_photo)
    @item.update!(cover_photos: cover_photos)
    render status: 200, json: { message: I18n.t('validation.cover_photo.delete_success'), 
      data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def delete_variant
    variant = @item.item_variants.find_by_id(ShortUUID.expand(params['variant_id']))
    if variant.nil?
      render status: 404, json: { message: I18n.t('item.variant.delete_failed'), reason: I18n.t('item.variant.not_found') }
      return
    end
    
    variant.destroy!
    render status: 200, json: { message: I18n.t('item.variant.delete_success'), data: { item: @item.as_json('admin_item', fetch_options({ 'item_type' => @item.item_type })) } }
  end

  def delete
    if params['reason'].blank?
      render status: 400, json: { message: I18n.t('validation.invalid_request'), reason: { 
        reason: [I18n.t('validation.required', param: 'Reason')] } }
      return
    end
    @item.deleted = true
    @item.meta['deletion_reason'] = params['reason']
    @item.save!
    render status: 200, json: { message: I18n.t('item.delete_success') }
  end

  def meta
    render status: 200, json: { message: 'success', data: fetch_options({ 'export_to' => 'array' })['configs'] }
  end

  private

  def load_shop
    @shop = AdminUser.current.shops.find_by_id(params['shop_id'])
    if @shop.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    elsif @shop.is_blocked?
      render status: 403, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.blocked', platform: PlatformConfig['name']) }
      return
    end
  end

  def load_item
    @item = @shop.items.find_by_id(ShortUUID.expand(params['id']))
    if @item.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('item.not_found') }
      return
    end
  end

  def fetch_options options = {}
    return { 'configs' => ItemConfig.fetch_all_configs('admin_item', options) }
  end
end
