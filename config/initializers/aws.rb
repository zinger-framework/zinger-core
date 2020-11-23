$s3_client = Aws::S3::Client.new(access_key_id: AwsConfig['access_key'], secret_access_key: AwsConfig['secret_key'], region: AwsConfig['region'])
$s3_signer = Aws::S3::Presigner.new(client: $s3_client)
