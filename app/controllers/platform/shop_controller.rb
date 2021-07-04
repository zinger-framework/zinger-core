class Platform::ShopController < PlatformController
  before_action :load_shop, except: :index
  before_action :is_shop_deleted?, only: [:update, :delete]

  def index
    conditions = ValidateParam::Shop.load_conditions(params)
    if conditions.class == String
      render status: 400, json: { success: false, message: I18n.t('shop.fetch_failed'), reason: conditions }
      return
    end

    query, shops = Shop.all.preload(:shop_detail).where(conditions), []
    total = query.count
    shops = query.offset(params['offset'].to_i).limit(LIMIT).order("id #{params['sort_order'].to_s.upcase == 'DESC' ? 'DESC' : 'ASC'}")
      .map { |shop| shop.as_json('platform_shop') } if total > 0

    render status: 200, json: { success: true, message: 'success', data: { shops: shops, total: total, per_page: LIMIT } }
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
