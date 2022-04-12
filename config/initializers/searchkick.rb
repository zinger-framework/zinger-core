Searchkick.client = Elasticsearch::Client.new(host: 'elasticsearch:9200', retry_on_failure: true, transport_options: {request: {timeout: 250}})
