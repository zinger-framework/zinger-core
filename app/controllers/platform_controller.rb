class PlatformController < ApplicationController
  before_action :reset_thread, :authenticate_request, :check_limit, :check_version
  TWO_FACTOR_SCREENS = ['platform/auth#verify_otp', 'platform/auth/otp#login']
  LIMIT = 25

  private

  def authenticate_request
    platform_user, @payload = PlatformUserSession.fetch_platform_user(request.headers['Authorization'])
    if platform_user.nil?
      render status: 401, json: { success: false, message: I18n.t('validation.invalid', param: 'authorization'), reason: 'UNAUTHORIZED' }
      return
    elsif platform_user.is_blocked?
      render status: 403, json: { success: false, message: I18n.t('auth.account_blocked', platform: PlatformConfig['name']), reason: 'UNAUTHORIZED' }
      return
    end

    platform_user.make_current

    if TWO_FACTOR_SCREENS.include?("#{params['controller']}##{params['action']}")
      if !platform_user.two_fa_enabled
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('auth.two_factor.already_disabled') }
        return
      elsif @payload['two_fa']['status'] != PlatformUser::TWO_FA_STATUSES['UNVERIFIED']
        render status: 200, json: { success: false, reason: 'ALREADY_LOGGED_IN', message: I18n.t('auth.otp.already_verified') }
        return
      end
    elsif params['action'] != 'logout' && platform_user.two_fa_enabled && @payload['two_fa']['status'] != PlatformUser::TWO_FA_STATUSES['VERIFIED']
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
    PlatformUser.reset_current
  end
end
