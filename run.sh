#!/bin/bash

bundle install

bundle exec rails db:migrate

rm -f tmp/pids/server.pid

bundle exec rails s -p 80 -b '0.0.0.0' &
SIDEKIQ_LOG=$SIDEKIQ_LOG bundle exec sidekiq 2>&1 >> log/$SIDEKIQ_LOG.log &

tail -f log/$SIDEKIQ_LOG.log
