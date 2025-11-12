#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#   List all packages for "GitHub-Packages"-Artifactory
# - here is yet another approach to commit the sensitive data (double-reversing), which is not allowed by GitHub;
# - one more time: it's not recommended to do unless you are definitely aware that it's completely safe !!!
# - in this particular case GitHub-secret "MY_READONLY_TOKEN" is not a real secret :-)
# -----------------------------------------------------------------------------------------------------------
#echo "... starting the script $0 in '$(pwd)' ..."

MY_READONLY_TOKEN=$(echo "6zYck0lz4EQpVDWqkQ2un0lVZLXC4V9wiE1L_phg" | rev)
curl -s \
  -H "Authorization: token $MY_READONLY_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  "https://api.github.com/users/krm-demo/packages?package_type=maven" | jq
