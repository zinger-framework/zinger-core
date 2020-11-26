Rails.application.routes.draw do
  root to: 'application#home'

  namespace :v2 do
    scope module: 'api', constraints: { subdomain: AppConfig['api_subdomain'] } do
      # Modify CONFIGS in ratelimit.rb when any action is added/changed
      namespace :auth do
        resources :otp, only: :none do
          collection do
            post :signup
            post :login
            post :reset_password
            post :reset_profile
          end
        end

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
      end

      resources :customer, only: :none do
        collection do
          get :profile
          put :profile, to: 'customer#update_profile'
          put :reset_profile
          put :password
          resources :session, only: [:index, :destroy]
        end
      end

      resources :shop, only: [:index, :show]
    end
  end

  scope module: 'admin', constraints: { subdomain: AppConfig['admin_subdomain'] } do
    get :dashboard
    resources :customer, only: [:index, :update, :destroy]
    resources :auth, only: :index do
      collection do
        post :login
        get :otp
        post :otp, to: 'auth#otp_login'
        post :resend_otp
        delete :logout
      end
    end

    resources :shop, only: [:index, :create, :update, :destroy] do
      collection do 
        get :add_shop
      end
      member do
        put :location
        put :icon
        put :cover_photo
        put :payment
        put :meta
        delete :icon, to: 'shop#delete_icon'
        delete :cover_photo, to: 'shop#delete_cover_photo'
      end
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']
  get '/*path', to: 'admin#dashboard', constraints: { subdomain: AppConfig['admin_subdomain'] }
  get '/*path', to: 'application#home', constraints: { subdomain: AppConfig['api_subdomain'] }
end
