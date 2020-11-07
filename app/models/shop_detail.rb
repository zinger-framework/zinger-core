class ShopDetail < ApplicationRecord
  belongs_to :shop

  def as_json purpose = nil
    case purpose
    when 'ui_shop_detail'
      time = Time.now.in_time_zone(PlatformConfig['time_zone']).strftime('%H:%M')
      opening_time = self.opening_time.in_time_zone(PlatformConfig['time_zone'])
      closing_time = self.closing_time.in_time_zone(PlatformConfig['time_zone'])
      return { 'address' => self.address, 'landline' => self.landline, 'mobile' => self.mobile, 'cover_photos' => self.cover_photos,
        'opening_time' => opening_time.strftime('%I:%M %p'), 'closing_time' => closing_time.strftime('%I:%M %p'),
        'open_now' => opening_time.strftime('%H:%M') <= time && time < closing_time.strftime('%H:%M') }
    end
  end
end
