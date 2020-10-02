class ApplicationController < ActionController::Base
  def home
    render html: 'home'
  end
end
