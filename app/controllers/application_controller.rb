class ApplicationController < ActionController::Base
  protect_from_forgery

  def home
    render html: 'Welcome to Zinger - Hyperlocal Delivery Framework'
  end
end
