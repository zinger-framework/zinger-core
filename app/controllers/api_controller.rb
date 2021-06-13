class ApiController < ApplicationController
  before_action :reset_thread, :authenticate_request, :check_limit, :check_version

  private

  def authenticate_request
    customer = CustomerSession.fetch_customer(request.headers['Authorization'])
    if customer.nil?
      render status: 401, json: { success: false, message: I18n.t('validation.invalid', param: 'authorization'), reason: 'UNAUTHORIZED' }
      return
    elsif customer.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.account_blocked', platform: PlatformConfig['name']), reason: 'UNAUTHORIZED' }
      return
    end

    customer.make_current
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      render status: 429, json: { success: false, message: I18n.t('validation.invalid_request'), reason: resp }
      return
    end
  end

  def reset_thread
    Customer.reset_current
  end

  def check_version
    raise VersionCake::ObsoleteVersionError.new '' if request_version.to_i != 2
  end
end
