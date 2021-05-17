class ApplicationController < ActionController::Base
  protect_from_forgery
  rescue_from VersionCake::UnsupportedVersionError, :with => :render_unsupported_version
  rescue_from VersionCake::ObsoleteVersionError, :with => :render_obsolete_version

  def home
    render html: 'Welcome to Zinger - Hyperlocal Delivery Framework'
  end

  private

  def render_unsupported_version
    render status: 422, json: { success: false, message: "You have requested an unsupported version (#{request_version})" }
    return
  end

  def render_obsolete_version
    render status: 422, json: { success: false, message: "You have requested an outdated version (#{request_version})" }
    return
  end
end
