class Core::Storage
  def self.upload_file key, file, options = {}
    $s3_client.put_object(bucket: options[:bucket] || AwsConfig['asset_bucket'], key: key, body: file) if Rails.env.production?
  end

  def self.fetch_url key, options = {}
    return $s3_signer.presigned_url(:get_object, bucket: options[:bucket] || AwsConfig['asset_bucket'], key: key) if Rails.env.production?
    return "https://dev.#{AwsConfig['region']}.amazonaws.com/#{options[:bucket] || AwsConfig['asset_bucket']}/#{key}"
  end

  def self.delete_file key, options = {}
    $s3_client.delete_object(bucket: options[:bucket] || AwsConfig['asset_bucket'], key: key) if Rails.env.production?
  end
end
