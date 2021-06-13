class AdminController < ApplicationController
  before_action :reset_thread, :authenticate_request, :check_limit, :check_version
  LIMIT = 25

  private

  def authenticate_request
    admin_user, @payload = AdminUserSession.fetch_admin_user(request.headers['Authorization'])
    if admin_user.nil?
      render status: 401, json: { success: false, message: I18n.t('validation.invalid', param: 'authorization'), reason: 'UNAUTHORIZED' }
      return
    elsif admin_user.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.account_blocked', platform: PlatformConfig['name']), reason: 'UNAUTHORIZED' }
      return
    end

    admin_user.make_current

    request_pattern = "#{params['controller']}##{params['action']}"
    if request_pattern == 'admin/auth#verify_otp' || (request_pattern == 'admin/auth#otp' && params['purpose'] == 'TWO_FA')
      if !admin_user.two_fa_enabled
        render status: 200, json: { success: false, message: I18n.t('auth.two_factor.already_disabled'), reason: 'ALREADY_LOGGED_IN' }
        return
      elsif @payload['two_fa']['status'] != AdminUser::TWO_FA_STATUSES['UNVERIFIED']
        render status: 200, json: { success: false, message: I18n.t('auth.otp.already_verified'), reason: 'ALREADY_LOGGED_IN' }
        return
      end
    elsif params['action'] != 'logout' && admin_user.two_fa_enabled && @payload['two_fa']['status'] != AdminUser::TWO_FA_STATUSES['VERIFIED']
      render status: 401, json: { success: false, message: I18n.t('auth.otp.unverified'), reason: 'OTP_UNVERIFIED' }
      return
    end
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      render status: 429, json: { success: false, message: I18n.t('validation.invalid_request'), reason: resp }
      return
    end
  end

  def check_version
    raise VersionCake::ObsoleteVersionError.new '' if request_version.to_i != 1
  end

  def reset_thread
    AdminUser.reset_current
  end
end
