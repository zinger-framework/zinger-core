class ShopDetail < ApplicationRecord
  belongs_to :shop
  validate :validations

  def as_json purpose = nil
    case purpose
    when 'ui_shop_detail'
      time = Time.now.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M')
      opening_time = self.opening_time.in_time_zone(PlatformConfig['time_zone'])
      closing_time = self.closing_time.in_time_zone(PlatformConfig['time_zone'])
      return { 'address' => self.address, 'telephone' => self.telephone, 'mobile' => self.mobile, 
        'cover_photos' => aws_key_path.map { |cover_photo_key| Core::Storage.fetch_url(cover_photo_key) },
        'opening_time' => opening_time.strftime('%I:%M %p'), 'closing_time' => closing_time.strftime('%I:%M %p'),
        'open_now' => opening_time.strftime('%H:%M') <= time && time < closing_time.strftime('%H:%M') }
    when 'admin_shop_detail'
      return { 'mobile' => self.mobile, 'opening_time' => self.opening_time.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M'), 
        'closing_time' => self.closing_time.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M') }
    end
  end

  def aws_key_path index = nil
    return "shop-cover/#{self.shop_id}/#{self.cover_photos.to_a[index]}" if index.present?
    return self.cover_photos.to_a.map { |cover_photo| "shop-cover/#{self.shop_id}/#{cover_photo}" }
  end

  private

  def validations
    return errors.add(:address, I18n.t('validation.required', param: 'Address')) if self.address.blank?

    self.address['number'] = self.address['number'].to_s.strip
    return errors.add(:number, I18n.t('validation.required', param: 'Shop number')) if self.address['number'].blank?

    self.address['street'] = self.address['street'].to_s.strip
    return errors.add(:street, I18n.t('validation.required', param: 'Street name')) if self.address['street'].blank?

    self.address['area'] = self.address['area'].to_s.strip
    return errors.add(:area, I18n.t('validation.required', param: 'Area name')) if self.address['area'].blank?

    self.address['city'] = self.address['city'].to_s.strip
    return errors.add(:city, I18n.t('validation.required', param: 'City')) if self.address['city'].blank?

    self.address['pincode'] = self.address['pincode'].to_s.strip
    return errors.add(:pincode, I18n.t('validation.required', param: 'Pincode')) if self.address['pincode'].blank?
    return errors.add(:pincode, I18n.t('validation.invalid', param: 'pincode')) if self.address['pincode'].length != 6

    self.telephone = self.telephone.to_s.strip
    self.mobile = self.mobile.to_s.strip
    return errors.add(:mobile, I18n.t('validation.invalid', param: 'mobile number')) unless self.mobile.match(MOBILE_REGEX)
  end
end
