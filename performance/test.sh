#!/usr/bin/env sh

# optional first argument is a base URL for the endpoints to be tested
# remember to quote the URL, so for example:
# ./test.sh 'http://localhost'

URL='https://test.openlmis.org'

run_tests() {
    export BASE_URL="${URL}"
    export DURATION=$1

    docker run --rm -e BASE_URL -v $(pwd):/bzt-configs \
      -v $(pwd)/../build/performance-artifacts:/tmp/artifacts \
      undera/taurus \
      -o modules.jmeter.properties.base-uri="${BASE_URL}" \
      -o modules.jmeter.properties.scenario-duration="${DURATION}" \
      -o reporting.2.dump-xml=/tmp/artifacts/stats.xml \
      config.yml \
      tests/*.yml
}

# determine base uri from first argument or use default
if [ ! -z ${1+x} ]; then
  URL=$1
fi

echo "Running performance tests against: $URL"

echo "Warm up (60s)"
run_tests 60

echo "Wait (30s)"
sleep 30

echo "Tests (180s)"
run_tests 180
