class Platform::ItemConfigController < PlatformController
  def index
    render status: 200, json: { message: 'success', data: ItemConfig.fetch_all_configs('platform_list', { 'export_to' => 'array' }) }
  end

  def create
    params['reference_id'] ||= params['title'].to_s.underscore.parameterize.dasherize
    item_config = ItemConfig.create(item_type: params['item_type'], key: params['key'], value: params['reference_id'], 
      meta: { title: params['title'] })
    if item_config.errors.any?
      render status: 400, json: { message: I18n.t('item.config.create_failed'), reason: item_config.errors.messages }
      return
    end

    render status: 200, json: { message: 'success', data: { item_config: item_config.as_json('platform_detail') } }
  end

  def destroy
    item_config = ItemConfig.find_by_id(ShortUUID.expand(params['id']))
    if item_config.nil?
      render status: 404, json: { message: I18n.t('item.config.delete_failed'), reason: I18n.t('item.config.not_found') }
      return
    end
    
    item_config.destroy!
    render status: 200, json: { message: I18n.t('item.config.delete_success') }
  end
end
