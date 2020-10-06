class ApiController < ApplicationController
  protect_from_forgery
  before_action :authenticate_request, :check_limit, :check_origin

  LIMIT = 20

  private
  def authenticate_request
    user = UserSession.fetch_user(request.headers['Authorization'])
    if user.nil?
      render status: 401, json: { success: false, message: 'Invalid Authorization', reason: 'UNAUTHORIZED' }
      return
    end

    user.make_current
  end

  def check_limit
  end

  def check_origin
    if request.headers['Origin'] != request.base_url
      render status: 403, json: { success: false, message: 'Unauthorized Origin', reason: 'UNAUTHORIZED' }
      return
    end
  end
end
