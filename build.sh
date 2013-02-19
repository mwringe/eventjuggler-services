#!/bin/bash -e

DIR=$(dirname $(readlink -f "$0"))
cd $DIR

mvn clean install
mkdir -p build/target/modules

cp -r analytics/as7/target/module/ build/target/modules/
cp standalone.xml build/target/

cd build/target

zip -r eventjuggler-services-as7.zip modules standalone.xml

echo ""
echo "To install, extract build/target/eventjuggler-services-as7.zip to jboss installation directory"
