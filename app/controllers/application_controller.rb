class ApplicationController < ActionController::Base
  before_action :reset_thread

  def home
    render html: 'Welcome to Zinger - Hyperlocal Delivery Framework'
  end

  private
  def reset_thread
    User.reset_current
  end
end
