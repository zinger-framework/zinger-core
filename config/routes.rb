Rails.application.routes.draw do
  root to: 'application#home'

  namespace :v2 do
    namespace :auth, only: :none, constraints: {subdomain: AppConfig['api_subdomain'], format: :json} do
      post :signup, to: 'signup#create'
      post :login, to: 'login#create'
      delete :logout
      post :send_otp
      
      post :forgot_password
      post :reset_password
      post :verify_email
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']

  get '/reset_password/:token', to: 'v2/auth#verify_reset_link', as: :verify_reset_link, constraints: { subdomain: AppConfig['api_subdomain'] }
  get '/email_verification/:token', to: 'v2/auth#email_verification', as: :verify_email_link, constraints: { subdomain: AppConfig['api_subdomain'] }
  get '/*path', to: 'application#home', constraints: { subdomain: AppConfig['api_subdomain'] }
end
