#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   List all packages for "GitHub-Packages"-Artifactory
# -------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

LAST_VERSION_NAME=$(gh api \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  /user/packages/maven/io.github.krm-demo.core-utils/versions \
  --jq '.[0].name')

LAST_VERSION_ID=$(gh api \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  /user/packages/maven/io.github.krm-demo.core-utils/versions \
  --jq '.[0].id')

echo "- going to delete the version '${LAST_VERSION_NAME}' with ID '${LAST_VERSION_ID}' ..."

gh api \
  --method DELETE \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  /user/packages/maven/io.github.krm-demo.core-utils/versions/${LAST_VERSION_ID}
