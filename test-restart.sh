#!/bin/bash

~/apps/nexus/current/bin/jsw/macosx-universal-32/nexus restart || exit 4

tail -f ~/apps/nexus/current/logs/wrapper.log