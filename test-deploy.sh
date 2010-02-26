#!/bin/bash

mvn -Dmaven.test.skip=true clean install || exit 1
rm -rf ~/apps/nexus/sonatype-work/nexus/plugin-repository/nexus-capture-plugin* || exit 2

unzip nexus-capture-plugin/target/nexus-capture-plugin-1.0-SNAPSHOT-bundle.zip -d ~/apps/nexus/sonatype-work/nexus/plugin-repository || exit 3

~/apps/nexus/current/bin/jsw/macosx-universal-32/nexus restart || exit 4

tail -f ~/apps/nexus/current/logs/wrapper.log