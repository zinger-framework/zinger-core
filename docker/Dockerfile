FROM ruby:2.7
RUN apt-get update -qq && apt-get install -y nodejs postgresql-client
RUN apt-get install -y yarn
RUN mkdir /zinger
WORKDIR /zinger
ADD . /zinger
RUN bundle install
# Add a script to be executed every time the container starts.
COPY docker/run.sh /usr/bin/
RUN chmod +x /usr/bin/run.sh
