#!/usr/bin/env bash

ROOT=$(cd $(dirname $0) && pwd)

fetch() {
	fetch_maven_deps() {
		local dir=$(mktemp -d);

		(
			cd "$dir";
			cat > pom.xml <<-EOF
				<project>
					<modelVersion>4.0.0</modelVersion>
					<groupId>temp</groupId>
					<artifactId>temp</artifactId>
					<version>master</version>
					<repositories>
						<repository>
							<id>jitpack.io</id>
							<url>https://jitpack.io</url>
						</repository>
					</repositories>
					<dependencies>
			EOF

			for package in "$@"; do
				echo "$package" | \
					sed -E 's!([^:]+):([^:]+):([^:]+)!<dependency><groupId>\1</groupId><artifactId>\2</artifactId><version>\3</version></dependency>!g' \
					>> pom.xml
			done;

			cat >> pom.xml <<-EOF
					</dependencies>
				</project>
			EOF

			mvn dependency:copy-dependencies
			mv target/dependency/* "$ROOT";
		)
		rm -rf "$dir";
	}

	fetch_maven_deps \
		'junit:junit:4.12' \

	curl -sL https://github.com/drm/java-redis-client/releases/download/v2.0.2/java-redis-client-v2.0.2--javac-1.8.0_212.jar -o ./java-redis-client-v2.0.2.jar
}

clean() {
	rm -f *.jar;
}

if [[ "$1" == "" ]]; then
	echo "Usage: ${0} [clean] fetch"
	exit 1
fi

set -x
set -e


for a in $@; do
	$a;
done
