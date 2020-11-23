class AdminController < ApplicationController
  before_action :set_title, :authenticate_request, :check_limit

  LIMIT = 20

  def dashboard
    @title = 'Dashboard'
  end

  private

  def authenticate_request
  end

  def check_limit
    resp = Core::Ratelimit.reached?(request)
    if resp
      render status: 429, json: { success: false, message: resp }
      return
    end
  end

  def set_title
    @header = { links: [] }
  end
end
