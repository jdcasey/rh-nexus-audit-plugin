#!/bin/bash

curl --basic -H "Accept: application/json" --include 'http://admin:admin123@localhost:8081/nexus/service/local/capture/central/public/echo?format=json&list=true&user=foobar'
echo ""