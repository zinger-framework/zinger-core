class ApiController < ApplicationController
  protect_from_forgery
  before_action :check_origin, :authenticate_request, :verify_user, :check_limit

  LIMIT = 20

  private

  def authenticate_request
  end

  def verify_user
  end

  def check_limit
  end

  def check_origin
  end
end
