class V2::Api::ShopController < ApiController
  LIMIT = 5
  DISTANCE = 5

  def index
    options = { where: { status: Shop::STATUSES['ACTIVE'], deleted: false }, limit: LIMIT, offset: params['offset'].to_i }
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
      total: shops.total_count, per_page: LIMIT } }
  end

  def show
    shop = Shop.find_by_id(params['id'])
    if shop.nil?
      render status: 200, json: { success: false, message: I18n.t('shop.not_found') }
      return
    end

    render status: 200, json: { success: true, message: 'success', data: shop.as_json('ui_shop_detail') }
  end
end
