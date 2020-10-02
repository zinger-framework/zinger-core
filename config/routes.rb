Rails.application.routes.draw do
  root to: 'application#home'

  namespace :v2 do
    namespace :auth, only: :none, constraints: {subdomain: AppConfig['api_subdomain'], format: :json} do
      post :signup, to: 'signup#create'
      post :login, to: 'login#create'
      post :send_otp
    end
  end

  mount Sidekiq::Web => '/sidekiq', subdomain: SidekiqSettings['subdomain']
end
