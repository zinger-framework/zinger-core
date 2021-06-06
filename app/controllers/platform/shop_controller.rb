class Platform::ShopController < PlatformController
  before_action :load_shop, except: :index
  before_action :is_shop_deleted?, only: [:update, :destroy]

  def index
    conditions = ValidateParam::Shop.load_conditions(params)
    if conditions.class == String
      render status: 400, json: { success: false, message: I18n.t('shop.fetch_failed'), reason: conditions }
      return
    end

    query, shops = Shop.unscoped.all.preload(:shop_detail).where(conditions), []
    total = query.count
    shops = query.offset(params['offset'].to_i).limit(LIMIT).order("id #{params['sort_order'].to_s.upcase == 'DESC' ? 'DESC' : 'ASC'}")
      .map { |shop| shop.as_json('platform_shop') } if total > 0

    render status: 200, json: { success: true, message: 'success', data: { shops: shops, total: total, page_size: LIMIT } }
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
        @shop.status = Shop::STATUSES[params['status'].to_s.strip.upcase]
      end
      if params['status'] == 'REJECTED'
        if params['reason'].present?
          shop_detail.meta['approval_comments'] ||= []
          shop_detail.meta['approval_comments'] << { 'message' => params['reason'], 'time' => Time.now.utc.strftime('%Y-%m-%d %H:%M:%S'), 
            'user_id' => PlatformUser.current.id, 'type' => 'Platform' }
        else
          reason = reason.merge({ reason: [I18n.t('validation.required', param: 'Reason')] })
        end
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

  def destroy
    @shop.update!(deleted: true)
    render status: 200, json: { success: true, message: I18n.t('shop.delete_success') }
  end

  private

  def load_shop
    @shop = Shop.unscoped.find_by_id(params['id'])
    if @shop.nil?
      render status: 404, json: { success: false, message: I18n.t('shop.not_found') }
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
