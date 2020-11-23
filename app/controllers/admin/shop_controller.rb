class Admin::ShopController < AdminController
  before_action :set_title
  before_action :load_shop, except: [:index, :create, :add_shop]

  def index
    @header[:title] = 'Shops'
    @header[:links].map{ |link| link[:active] = false }

    @shop = Shop.unscoped.find_by_id(params['q']) if params['q'].present?
    @error = nil
  end

  def create
    shop, shop_detail = nil, nil
    ActiveRecord::Base.transaction do
      shop = Shop.new(name: params['name'], lat: params['lat'], lng: params['lng'], tags: params['tags'],
        icon: "shop-icon-#{Time.now.to_i}#{File.extname(params['file'].path)}")
      shop_detail = shop.build_shop_detail(address: { number: params['number'], street: params['street'], area: params['area'],
        city: params['city'], pincode: params['pincode'] }, telephone: params['telephone'], mobile: params['mobile'],
        opening_time: Time.find_zone(PlatformConfig['time_zone']).strptime(params['opening_time'], '%H:%M').utc, 
        closing_time: Time.find_zone(PlatformConfig['time_zone']).strptime(params['closing_time'], '%H:%M').utc)
      shop_detail.validate
      shop.save unless shop_detail.errors.any?
    end

    if shop.errors.any? || shop_detail.errors.any?
      flash[:error] = shop.errors.messages.values.flatten.first || shop_detail.errors.messages.values.flatten.first
      return redirect_to add_shop_shop_index_path
    end

    File.open(params['file'].path, 'rb') { |file| Core::Storage.upload_file(shop.aws_key_path, file) }
    flash[:success] = 'Shop creation is successful'
    redirect_to shop_index_path(q: shop.id)
  end

  def add_shop
    @header[:title] = 'Add New Shop'
    @header[:links].map{ |link| link[:active] = false }
    @header[:links][0][:active] = true
  end

  def update
    @shop.update(name: params['name'], status: params['status'], tags: params['tags'], updated_at: Time.now.utc)
    shop_detail = @shop.shop_detail
    shop_detail.update(mobile: params['mobile'], opening_time: Time.find_zone(PlatformConfig['time_zone']).strptime(params['opening_time'], '%H:%M').utc, 
      closing_time: Time.find_zone(PlatformConfig['time_zone']).strptime(params['closing_time'], '%H:%M').utc)

    if @shop.errors.any? || shop_detail.errors.any?
      flash[:error] = @shop.errors.messages.values.flatten.first || shop_detail.errors.messages.values.flatten.first
      return redirect_to shop_index_path(q: params['id'])
    end

    flash[:success] = 'Shop update is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def location
    @shop.update(lat: params['lat'], lng: params['lng'], updated_at: Time.now.utc)
    shop_detail = @shop.shop_detail
    shop_detail.update(address: { number: params['number'], street: params['street'], area: params['area'],
      city: params['city'], pincode: params['pincode'] }, telephone: params['telephone'])

    if @shop.errors.any? || shop_detail.errors.any?
      flash[:error] = @shop.errors.messages.values.flatten.first || shop_detail.errors.messages.values.flatten.first
      return redirect_to shop_index_path(q: params['id'])
    end

    flash[:success] = 'Shop location update is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def destroy
    @shop.update!(deleted: true)
    flash[:success] = 'Deletion is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def icon
    @shop.update!(icon: "shop-icon-#{Time.now.to_i}#{File.extname(params['file'].path)}")
    File.open(params['file'].path, 'rb') { |file| Core::Storage.upload_file(@shop.aws_key_path, file) }
    flash[:success] = 'Icon upload is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def cover_photo
    @shop.shop_detail.update!(cover_photos: @shop.shop_detail.cover_photos.to_a << "shop-cover-#{Time.now.to_i}#{File.extname(params['file'].path)}")
    @shop.update!(updated_at: Time.now.utc)
    File.open(params['file'].path, 'rb') { |file| Core::Storage.upload_file(@shop.shop_detail.aws_key_path(@shop.shop_detail.cover_photos.size - 1), file) }
    flash[:success] = 'Cover photo upload is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def payment
    @shop.shop_detail.update!(payment: JSON.parse(params['payment']))
    @shop.update!(updated_at: Time.now.utc)
    flash[:success] = 'Payment update is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def meta
    @shop.shop_detail.update!(meta: JSON.parse(params['meta']))
    @shop.update!(updated_at: Time.now.utc)
    flash[:success] = 'Meta update is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def delete_icon
    @shop.update!(icon: nil)
    flash[:success] = 'Icon deletion is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  def delete_cover_photo
    if @shop.shop_detail.cover_photos.blank?
      flash[:error] = 'Cover photo is already empty'
      return redirect_to shop_index_path(q: params['id'])
    end
    
    @shop.shop_detail.cover_photos.delete_at(params['index'].to_i)
    @shop.shop_detail.update!(cover_photos: @shop.shop_detail.cover_photos)
    @shop.update!(updated_at: Time.now.utc)
    flash[:success] = 'Cover photo deletion is successful'
    redirect_to shop_index_path(q: params['id'])
  end

  private

  def set_title
    @header = { links: [ { title: 'Add Shop', path: add_shop_shop_index_path } ] }
  end

  def load_shop
    @shop = Shop.fetch_by_id(params['id'])
    if @shop.nil?
      flash[:error] = 'Shop is not found'
      return redirect_to shop_index_path(q: params['id'])
    end
  end
end
