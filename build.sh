#!/bin/bash -e

DIR=$(dirname $(readlink -f "$0"))
cd $DIR

rm -rf build/target

mvn clean install
mkdir -p build/target/modules
mkdir -p build/target/standalone/configuration

cp -r analytics/as7/target/module/* build/target/modules/
cp standalone.xml build/target/standalone/configuration/

cd build/target

zip -r eventjuggler-services-as7.zip modules standalone

echo ""
echo "To install, extract build/target/eventjuggler-services-as7.zip to jboss installation directory"
