class Api::ShopController < ApiController
  LIMIT = 5
  DISTANCE = 5

  def index
    params['page_size'] ||= LIMIT
    options = { where: { status: Shop::STATUSES['ACTIVE'], deleted: false }, limit: params['page_size'].to_i, offset: params['offset'].to_i,
      order: { id: params['sort_order'].to_s.upcase == 'DESC' ? 'DESC' : 'ASC' }, includes: :shop_detail }
    options[:where].merge!({ location: { near: { lat: params['lat'].to_f, lon: params['lng'].to_f }, 
      within: "#{params['distance'].to_i > 0 ? params['distance'].to_i : DISTANCE}km" } }) if params['lat'].present? &&
      params['lng'].present?

    shops = if params['name'].to_s.length > 3
      Shop.search(params['name'], options.merge({ fields: [:name], match: :word_start }))
    elsif params['tag'].present?
      Shop.search(params['tag'], options.merge({ fields: [:tags], misspellings: false }))
    else
      Shop.search(options)
    end

    render status: 200, json: { success: true, message: 'success', data: { shops: shops.map { |shop| shop.as_json('ui_shop') },
      total: shops.total_count } }
  end

  def show
    shop = Shop.fetch_by_id(params['id'])
    if shop.nil?
      render status: 404, json: { success: false, message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    end

    render status: 200, json: { success: true, message: 'success', data: { shop: shop.as_json('ui_shop_detail') } }
  end
end
