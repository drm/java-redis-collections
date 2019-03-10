#!/usr/bin/env bash

set -x
set -e

mkdir -p bin;

javac -cp 'lib/*' $(find src -name "*.java") $(find test -name "*.java") -d bin
cd bin && jar -cvf ./java-redis-collections-$(git describe)--$(javac -version 2>&1 | sed 's/[^a-z0-9._]\+/-/g').jar $(find . -name "*.class")
