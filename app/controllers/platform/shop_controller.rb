class Platform::ShopController < PlatformController
  before_action :load_shop, except: :index
  before_action :is_shop_deleted?, only: [:update, :delete]

  def index
    params['page_size'] ||= LIMIT
    filter_params = params['next_page_token'].present? ? JSON.parse(Base64.decode64(params['next_page_token'])) : params
    filter_params = filter_params.slice(*%w(start_date end_date statuses id include_deleted sort_order page_size next_id))

    query = ValidateParam::Shop.load_conditions(filter_params)
    if query.class == Hash
      render status: 400, json: { success: false, message: I18n.t('shop.fetch_failed'), reason: query }
      return
    end

    query, data = query.preload(:shop_detail), { shops: [] }
    data = data.merge({ total: query.count, page_size: params['page_size'].to_i }) if params['next_page_token'].blank?
    
    if params['next_page_token'].present? || data[:total] > 0
      data[:shops] = query.limit(filter_params['page_size'].to_i + 1).as_json('platform_shop_list')
      if data[:shops].size > filter_params['page_size'].to_i
        filter_params['next_id'] = data[:shops].pop['id']
        data[:next_page_token] = Base64.encode64(filter_params.to_json).gsub(/[^0-9a-z]/i, '')
      end
    end

    render status: 200, json: { success: true, message: 'success', data: data }
  end

  def show
    render status: 200, json: { success: true, message: 'success', data: { shop: @shop.as_json('platform_shop') } }
  end

  def update
    shop_detail, reason = @shop.shop_detail, {}
    if params['status'].present?
      if !%w(ACTIVE REJECTED BLOCKED).include?(params['status']) || (params['status'] == 'ACTIVE' && @shop.status_was == Shop::STATUSES['INACTIVE'])
        reason = reason.merge({ status: [I18n.t('validation.invalid', param: 'status')] })
      else
        if %w(REJECTED BLOCKED).include?(params['status'])
          if params['reason'].present?
            @shop.send("#{params['status'].downcase}_conversations").new(sender_id: PlatformUser.current.id, sender_type: 'PlatformUser', 
              message: params['reason'])
          else
            reason = reason.merge({ reason: [I18n.t('validation.required', param: 'Reason')] })
          end
        end
        @shop.status = Shop::STATUSES[params['status'].to_s.strip.upcase]
      end
    end

    @shop.validate
    reason = reason.merge(@shop.errors) if @shop.errors.any?

    if reason.present?
      render status: 400, json: { success: false, message: I18n.t('shop.update_failed'), reason: reason }
      return
    end

    shop_detail.save!(validate: false)
    @shop.save!(validate: false)
    render status: 200, json: { success: true, message: I18n.t('shop.update_success'), data: { shop: @shop.as_json('platform_shop') } }
  end

  def delete
    if params['reason'].blank?
      render status: 400, json: { success: false, message: I18n.t('validation.invalid_request'), reason: { 
        reason: [I18n.t('validation.required', param: 'Reason')] } }
      return
    end
    @shop.deleted = true
    @shop.deleted_conversations.new(sender_id: PlatformUser.current.id, sender_type: 'PlatformUser', message: params['reason'])
    @shop.save!
    render status: 200, json: { success: true, message: I18n.t('shop.delete_success'), data: { shop: @shop.as_json('platform_shop') } }
  end

  private

  def load_shop
    @shop = Shop.find_by_id(params['id'])
    if @shop.nil?
      render status: 404, json: { success: false, message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.not_found') }
      return
    end
  end

  def is_shop_deleted?
    if @shop.deleted
      render status: 400, json: { success: false, message: I18n.t('validation.invalid_request'), reason: I18n.t('shop.delete_failed') }
      return
    end
  end
end
