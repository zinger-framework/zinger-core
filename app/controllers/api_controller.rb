class ApiController < ApplicationController
  before_action :authenticate_request, :check_limit, :check_origin

  LIMIT = 20

  private

  def authenticate_request
    customer = CustomerSession.fetch_customer(request.headers['Authorization'])

    error_msg = if customer.nil?
      I18n.t('validation.invalid', param: 'authorization')
    elsif customer.is_blocked?
      I18n.t('customer.account_blocked', platform: PlatformConfig['name'])
    end

    if error_msg.present?
      render status: 401, json: { success: false, message: error_msg, reason: 'UNAUTHORIZED' }
      return
    end

    customer.make_current
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      render status: 429, json: { success: false, message: resp }
      return
    end
  end

  def check_origin
    if request.headers['Origin'] != request.base_url
      render status: 403, json: { success: false, message: 'Unauthorized Origin', reason: 'UNAUTHORIZED' }
      return
    end
  end
end
