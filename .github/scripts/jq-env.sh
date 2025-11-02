#!/usr/bin/env bash
echo "... starting the script $0 in '$(pwd)' ..."
echo "environment-variables as JSON:"
echo "------------------------------"
jq -n --sort-keys env
echo "------------------------------"
