#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   List all packages for "GitHub-Packages"-Artifactory
#   TODO: the bullshit below is not working (and will never be working) - use direct HTTP like
#   TODO: maven-dependency-plugin does when executing `...> mvn dependency:get ...`
# -------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

gh api \
  -H "Accept: application/vnd.github.v3.raw" \
  /repos/OWNER/REPO/packages/maven/GROUP_ID_PATH/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION.jar \
  --output ARTIFACT_ID-VERSION.jar

gh api \
  -H "Accept: application/vnd.github.v3.raw" \
  /repos/krm-demo/core-utils/packages/maven/io/github/krm-demo/core-utils/21.23.002/core-utils.jar \
  --output ARTIFACT_ID-VERSION.jar

