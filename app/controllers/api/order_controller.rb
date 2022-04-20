class Api::OrderController < ApiController
  before_action :load_order, only: [:update, :show, :item, :delete_item, :destroy]

  def create
    reason = {}
    order = Customer.current.orders.new
    %w(shipping_addr billing_addr).each { |key| order.send("#{key}=", params[key].permit(*Order::ADDRESS_PARAMS)) }

    order.validate
    reason = order.errors if order.errors.any?
    
    order_items = order.order_items
    params['items'].to_a.each do |item|
      item = item.slice('item_id', 'item_variant_id', 'quantity').as_json
      %w(item_id item_variant_id).each { |key| item[key] = ShortUUID.expand(item[key]) if item[key].present? }
      order_item = order_items.new(item)
      order_item.validate
      reason = reason.merge(order_item.errors) if order_item.errors.any?
    end

    if reason.present?
      render status: 400, json: { message: I18n.t('order.create_failed'), reason: reason }
      return
    end

    ActiveRecord::Base.transaction do
      order.save!(validate: false)
      order_items.each { |order_item| order_item.save!(validate: false) }
    end
    render status: 200, json: { message: I18n.t('order.create_success'), data: { order: order.as_json } }
  end

  def index
    params['page_size'] ||= LIMIT
    filter_params = params['next_page_token'].present? ? JSON.parse(Base64.decode64(params['next_page_token'])) : params
    filter_params = filter_params.slice(*%w(start_date end_date order_status payment_status sort_order page_size next_id))
    filter_params['id'] = ShortUUID.expand(filter_params['id']) if filter_params['id'].present?

    query = ValidateParam::Order.load_conditions(filter_params, { 'parent' => Customer.current })
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
    %w(order_status rating).each { |key| @order.send("#{key}=", params[key]) if params[key].present? }
    %w(shipping_addr billing_addr).each { |key| @order.send("#{key}=", params[key].permit(*Order::ADDRESS_PARAMS)) if params[key].present? }

    @order.validate
    if @order.errors.any?
      render status: 400, json: { message: I18n.t('order.update_failed'), reason: @order.errors }
      return
    end

    @order.save!(validate: false)
    render status: 200, json: { message: I18n.t('order.update_success'), data: { order: @order.as_json } }
  end

  def item
    item_data = params.slice('item_id', 'item_variant_id', 'quantity').as_json
    %w(item_id item_variant_id).each { |key| item_data[key] = ShortUUID.expand(item_data[key]) if item_data[key].present? }

    order_item = @order.order_items.create(item_data)
    if order_item.errors.any?
      render status: 400, json: { message: I18n.t('order.update_failed'), reason: order_item.errors }
      return
    end

    render status: 200, json: { message: I18n.t('order.update_success'), data: { order: @order.as_json } }
  end

  def delete_item
    order_item = @order.order_items.find_by_id(ShortUUID.expand(params['item_id']))
    if order_item.nil?
      render status: 404, json: { message: I18n.t('item.not_found') }
      return
    end

    order_item.destroy!
    render status: 200, json: { message: I18n.t('order.update_success'), data: { order: @order.as_json } }
  end

  def destroy
    @order.update!(deleted: true)
    render status: 200, json: { message: I18n.t('order.delete_success') }
  end

  private

  def load_order
    @order = Customer.current.orders.undeleted.find_by_id(ShortUUID.expand(params['id']))
    if @order.nil?
      render status: 404, json: { message: I18n.t('validation.invalid_request'), reason: I18n.t('order.not_found') }
      return
    end
  end
end
