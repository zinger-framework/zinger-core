class AdminController < ApplicationController
  before_action :reset_thread, :authenticate_request, :check_limit, :check_version
  TWO_FACTOR_SCREENS = ['admin/auth#verify_otp', 'admin/auth/otp#login']

  private

  def authenticate_request
    admin_user, @payload = AdminUserSession.fetch_admin_user(request.headers['Authorization'])
    error_msg = if admin_user.nil?
      I18n.t('validation.invalid', param: 'authorization')
    elsif admin_user.is_blocked?
      I18n.t('auth.account_blocked', platform: PlatformConfig['name'])
    end
    
    if error_msg.present?
      render status: 401, json: { success: false, message: error_msg, reason: 'UNAUTHORIZED' }
      return
    end

    admin_user.make_current

    if TWO_FACTOR_SCREENS.include?("#{params['controller']}##{params['action']}")
      if !admin_user.two_fa_enabled
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('admin_user.two_factor.already_disabled') }
        return
      elsif @payload['two_fa']['status'] != AdminUser::TWO_FA_STATUSES['UNVERIFIED']
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('auth.otp.already_verified') }
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
      render status: 429, json: { success: false, message: resp }
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
