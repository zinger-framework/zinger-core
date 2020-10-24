class ApplicationController < ActionController::Base
  protect_from_forgery
  before_action :reset_thread

  def home
    render html: 'Welcome to Zinger - Hyperlocal Delivery Framework'
  end

  private
  def reset_thread
    Customer.reset_current
  end
end
