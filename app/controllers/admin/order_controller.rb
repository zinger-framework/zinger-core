class Admin::OrderController < AdminController
  before_action :load_shop
  before_action :load_order, except: :index

  def index
    params['page_size'] ||= LIMIT
    filter_params = params['next_page_token'].present? ? JSON.parse(Base64.decode64(params['next_page_token'])) : params
    filter_params = filter_params.slice(*%w(start_date end_date order_status payment_status sort_order page_size id next_id))
    filter_params['id'] = ShortUUID.expand(params['id']) if params['id'].present?

    query = ValidateParam::Order.load_conditions(filter_params, { 'parent' => @shop })
    if query.class == Hash
      render status: 400, json: { message: I18n.t('order.fetch_failed'), reason: query }
      return
    end

    query, data = query.preload(:order_items, :order_transactions), { orders: [] }
    data = data.merge({ total: query.count, page_size: params['page_size'].to_i }) if params['next_page_token'].blank?
    
    if params['next_page_token'].present? || data[:total] > 0
      data[:orders] = query.limit(filter_params['page_size'].to_i + 1).map { |order| order.as_json }
      if data[:orders].size > filter_params['page_size'].to_i
        filter_params['next_id'] = ShortUUID.expand(data[:orders].pop['id'])
        data[:next_page_token] = Base64.encode64(filter_params.to_json).gsub(/[^0-9a-z]/i, '')
      end
    end

    render status: 200, json: { message: 'success', data: data }
  end

  def show
    render status: 200, json: { message: 'success', data: { order: @order.as_json } }
  end

  def update
    @order.send("order_status=", params['order_status']) if params['order_status'].present?
    %w(shipping_addr billing_addr).each { |key| @order.send("#{key}=", params[key].permit(*Order::ADDRESS_PARAMS)) if params[key].present? }

    @order.validate
    if @order.errors.any?
      render status: 400, json: { message: I18n.t('order.update_failed'), reason: @order.errors }
      return
    end

    @order.save!(validate: false)
    render status: 200, json: { message: I18n.t('order.update_success'), data: { order: @order.as_json } }
  end

  private

  def load_shop
    @shop = AdminUser.current.shops.find_by_id(params['shop_id'])
    if @shop.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    elsif @shop.is_blocked? && request.method != :get
      render status: 403, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.blocked', platform: PlatformConfig['name']) }
      return
    end
  end

  def load_order
    @order = @shop.orders.undeleted.find_by_id(ShortUUID.expand(params['id']))
    if @order.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('order.not_found') }
      return
    end
  end
end
