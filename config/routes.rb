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

      resources :shop, only: [:index, :show] do
        resources :items, only: :index
      end

      resources :order, only: [:create, :index, :show, :update, :destroy] do
        member do
          post :item
          delete 'item/:item_id', to: 'order#delete_item'
        end
      end
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
        
        resources :item, only: [:index, :create, :update, :show] do
          collection { post :delete }
          member do
            post :icon
            post :cover_photo
            post :variant
            delete :icon, to: 'item#delete_icon'
            delete 'cover_photo/:cover_photo_id', to: 'item#delete_cover_photo'
            delete 'variant/:variant_id', to: 'item#delete_variant'
          end
        end

        resources :order, only: [:index, :update, :show]
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

      resources :shop, only: [:index, :show, :update] do
        collection { post :delete }
        
        resources :item, only: [:index, :show]
      end

      resources :item_config, only: [:index, :create, :destroy]
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']

  get '/admin/v:api_version/item/meta' => 'admin/item#meta', subdomain: AppConfig['admin_subdomain']
  get '/platform/v:api_version/item/meta' => 'platform/item#meta', subdomain: AppConfig['platform_subdomain']
  get '/shop/:id' => 'application#home', :as => :pl_shop_detail, subdomain: AppConfig['platform_subdomain']
  get '/*path', to: 'application#home'
end
