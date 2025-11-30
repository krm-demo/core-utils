#!/usr/bin/env bash
# ------------------------------------------------------------------------------------------------------------------
#      ... attempt to fetch all versions of GitHub-package using paging and subsequent queries ...
# ------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."



MY_READONLY_TOKEN=$(echo "6zYck0lz4EQpVDWqkQ2un0lVZLXC4V9wiE1L_phg" | rev)
curl -i \
  -H "Authorization: token $MY_READONLY_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?per_page=5

