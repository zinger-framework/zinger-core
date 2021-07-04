class ShopDetail < ApplicationRecord
  belongs_to :shop
  validate :validations

  def as_json purpose = nil, options = {}
    case purpose
    when 'ui_shop_detail'
      time = Time.now.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M')
      opening_time = self.opening_time.in_time_zone(PlatformConfig['time_zone'])
      closing_time = self.closing_time.in_time_zone(PlatformConfig['time_zone'])
      return { 'address' => self.address, 'telephone' => self.telephone, 'mobile' => self.mobile, 
        'cover_photos' => self.cover_photos.to_a.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
          'url' => Core::Storage.fetch_url(self.cover_photo_key_path(cover_photo)) } },
        'opening_time' => opening_time.strftime('%H:%M'), 'closing_time' => closing_time.strftime('%H:%M'),
        'open_now' => opening_time.strftime('%H:%M') <= time && time < closing_time.strftime('%H:%M') }
    when 'admin_shop_detail', 'platform_shop_detail'
      resp = { 'address' => self.address.merge(options.slice(*%w(lat lng))), 'telephone' => self.telephone, 'mobile' => self.mobile, 
        'opening_time' => self.opening_time.present? ? self.opening_time.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M') : nil, 
        'closing_time' => self.closing_time.present? ? self.closing_time.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M') : nil,
        'payment' => self.payment, 'description' => self.description,
        'cover_photos' => self.cover_photos.to_a.map { |cover_photo| { 'id' => cover_photo.split('-')[0].to_i, 
          'url' => Core::Storage.fetch_url(self.cover_photo_key_path(cover_photo)) } } }
      return resp
    end
  end

  def cover_photo_key_path cover_photo
    "shop/cover_photos/#{self.shop_id}/#{cover_photo}"
  end

  private

  def validations
    errors.add(:description, I18n.t('shop.description_long')) if self.description.present? && self.description.length > 250
    errors.add(:pincode, I18n.t('validation.invalid', param: 'pincode')) if self.address['pincode'].present? && self.address['pincode'].length != 6
    errors.add(:mobile, I18n.t('validation.invalid', param: 'mobile number')) if self.mobile.present? && !self.mobile.match(MOBILE_REGEX)
    errors.add(:telephone, I18n.t('validation.invalid', param: 'telephone')) if self.telephone.present? && !self.telephone.match(TELEPHONE_REGEX)
    errors.add(:account_number, I18n.t('validation.invalid', param: 'account number')) if self.payment['account_number'].present? && 
      !self.payment['account_number'].match(ACCOUNT_NUMBER_REGEX)
    errors.add(:account_ifsc, I18n.t('validation.invalid', param: 'account IFSC')) if self.payment['account_ifsc'].present? && 
      !self.payment['account_ifsc'].match(ACCOUNT_IFSC_REGEX)
    errors.add(:account_holder, I18n.t('validation.invalid', param: 'account holder')) if self.payment['account_holder'].present? &&
      !self.payment['account_holder'].match(/^[ a-zA-Z0-9]{4,35}$/)
    errors.add(:pan, I18n.t('validation.invalid', param: 'PAN')) if self.payment['pan'].present? && !self.payment['pan'].match(PAN_REGEX)
    errors.add(:gst, I18n.t('validation.invalid', param: 'GST')) if self.payment['gst'].present? && !self.payment['gst'].match(GST_REGEX)
  end
end
