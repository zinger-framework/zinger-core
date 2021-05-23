Rails.application.routes.draw do
  root to: 'application#home'

  scope module: 'api', constraints: { subdomain: AppConfig['api_subdomain'] } do
    scope 'v:api_version' do
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
    scope 'v:api_version' do
      namespace :auth do
        resources :otp, only: :none do
          collection do 
            post :login
            post :forgot_password
            post :verify_mobile
            post :signup
          end
        end
      end

      resources :auth, only: :none do
        collection do
          post :login
          post :verify_otp
          post :reset_password
          delete :logout
          post :signup
        end
      end

      resources :user_profile, only: :index do
        collection do
          post :reset_password
          post :modify
        end
      end

      resources :shop, only: [:new, :update, :destroy, :show] do
        member do
          post :icon
          post :cover_photo
          delete :icon, to: 'shop#delete_icon'
          delete 'cover_photo/:cover_photo_id', to: 'shop#delete_cover_photo'
        end
      end
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']
  get '/*path', to: 'application#home'
end
