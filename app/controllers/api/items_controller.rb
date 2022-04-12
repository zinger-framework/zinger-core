class Api::ItemsController < ApiController
  before_action :load_shop

  def index
    params['page_size'] ||= LIMIT
    options = { where: { status: :active, deleted: false, shop_id: @shop.id }, limit: params['page_size'].to_i, 
      offset: params['offset'].to_i, order: { id: params['sort_order'].to_s.upcase == 'DESC' ? 'DESC' : 'ASC' },
      includes: :item_variants }

    %w(category item_type).each do |key|
      options[:where].merge!({ key => Array.wrap(params[key]) }) if params[key].present?
    end
    
    params['filters'].as_json.to_h.each do |k, v|
      options[:where].merge!({ "filters.#{k}" => Array.wrap(v) })
    end

    items = if params['name'].to_s.length > 3
      Item.search(params['name'], options.merge({ fields: [:name], match: :text_middle }))
    else
      Item.search(options)
    end

    render status: 200, json: { message: 'success', data: { items: items.map { |item| item.as_json('ui_item') },
      total: items.total_count } }
  end

  private

  def load_shop
    @shop = Shop.where(status: Shop::STATUSES['ACTIVE'], deleted: false, id: params['shop_id']).first
    if @shop.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    end
  end
end
