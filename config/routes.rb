Rails.application.routes.draw do
  root to: 'application#home'

  scope module: 'api', constraints: { subdomain: AppConfig['api_subdomain'] } do
    scope 'v:api_version' do
      # Modify CONFIGS in ratelimit.rb when any action is added/changed
      resources :auth, only: :none do
        collection do
          post :otp
          post :signup
          post :login
          post :reset_password
          delete :logout
        end
      end

      resources :user_profile, only: :index do
        collection do
          put :modify
          put :reset_profile
          put :reset_password
          resources :session, only: [:index, :destroy]
        end
      end

      resources :shop, only: [:index, :show]
    end
  end

  namespace 'admin', constraints: { subdomain: AppConfig['api_subdomain'] } do
    scope 'v:api_version' do
      resources :auth, only: :none do
        collection do
          post :otp
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

      resources :shop, only: [:index, :new, :update, :show] do
        member do
          post :icon
          post :cover_photo
          delete :icon, to: 'shop#delete_icon'
          delete 'cover_photo/:cover_photo_id', to: 'shop#delete_cover_photo'
        end
      end
    end
  end

  namespace 'platform', constraints: { subdomain: AppConfig['api_subdomain'] } do
    scope 'v:api_version' do
      resources :auth, only: :none do
        collection do
          post :otp
          post :login
          post :verify_otp
          post :reset_password
          delete :logout
        end
      end

      resources :user_profile, only: :index do
        collection do
          post :reset_password
          post :modify
        end
      end

      resources :shop, only: [:index, :show, :update, :destroy]
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']

  get '/shop/:id' => 'application#home', :as => :pl_shop_detail, subdomain: AppConfig['platform_subdomain']
  get '/*path', to: 'application#home'
end
