#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   List all packages for "GitHub-Packages"-Artifactory
# -------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

gh api \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  /users/krm-demo/packages?package_type=maven
