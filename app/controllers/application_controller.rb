class ApplicationController < ActionController::Base
  def home
    render html: 'logesh'
  end

  def help
    render html: 'admin-help'
  end
end
