Rails.application.routes.draw do
  root to: 'application#home'

  namespace :v2 do
    namespace :auth, constraints: {subdomain: AppConfig['api_subdomain'], format: :json} do
      resources :signup, only: :none do
        collection do
          post :validate
          get '/verification/:token', action: :verify_link, as: :verify_signup_link
          post :signup
        end
      end
      resources :login, only: :none do
        collection do
          post :login
          delete :logout
        end
      end
      resources do
        post :forgot_password
        get '/reset_password/:token', action: :verify_reset_link
        post :reset_password
      end
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']
end
