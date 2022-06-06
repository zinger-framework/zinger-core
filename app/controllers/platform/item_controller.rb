class Platform::ItemController < PlatformController
  before_action :load_shop, except: :meta
  before_action :load_item, only: :show

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
      data[:items] = query.limit(filter_params['page_size'].to_i + 1).map { |item| item.as_json('platform_item_list') }
      if data[:items].size > filter_params['page_size'].to_i
        filter_params['next_id'] = ShortUUID.expand(data[:items].pop['id'])
        data[:next_page_token] = Base64.encode64(filter_params.to_json).gsub(/[^0-9a-z]/i, '')
      end
    end

    render status: 200, json: { message: 'success', data: data }
  end

  def show
    render status: 200, json: { message: 'success', data: { item: @item.as_json('platform_item') } }
  end

  def meta
    render status: 200, json: { message: 'success', data: ItemConfig.all.as_json }
  end

  private

  def load_shop
    @shop = Shop.find_by_id(params['shop_id'])
    if @shop.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
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
end
