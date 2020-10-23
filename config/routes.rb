Rails.application.routes.draw do
  root to: 'application#home'

  namespace :v2 do
    # Modify CONFIGS in ratelimit.rb when any action is added/changed
    namespace :auth, constraints: { subdomain: AppConfig['api_subdomain'] } do
      resources :signup, only: :none do
        collection do
          post :password
          post :otp
          post :google
        end
      end

      resources :login, only: :none do
        collection do
          post :password
          post :otp
          post :google
        end
      end

      delete :logout
      post :reset_password

      resources :otp, only: :none do
        collection do
          post :signup
          post :login
          post :reset_password
        end
      end
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']
  get '/*path', to: 'application#home', constraints: { subdomain: AppConfig['api_subdomain'] }
end
