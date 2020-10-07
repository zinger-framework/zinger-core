FROM ruby:2.7.2
RUN apt-get update -qq && apt-get install -y nodejs postgresql-client
RUN mkdir /zinger
WORKDIR /zinger
ADD . /zinger
RUN bundle install
