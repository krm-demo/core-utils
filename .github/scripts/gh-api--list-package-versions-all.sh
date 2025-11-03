#!/usr/bin/env bash
# ------------------------------------------------------------------------------------------------------------------
#   List all packages for "GitHub-Packages"-Artifactory with corresponding links:
#   - the result of "gh api ..." execution is saved in JSON-format in directory ".github/th-vars";
#   - this bash-script is invoked from GitHub-workflow "misc--gh-packages.yml";
#   - if executing locally the output-file with extension ".md" will be created in the directory of this script;
# ------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  GITHUB_STEP_SUMMARY=$(dirname $0)/GH-Packages-Versions-All.md
  # for debug/test purposes the local summary is always cleared
  rm -rf $GITHUB_STEP_SUMMARY
fi
echo "going to dump GitHub-Markdown-file into '$GITHUB_STEP_SUMMARY':"

GH_PACKAGE_VERSIONS_ALL=$(gh api \
  -H "Accept: application/vnd.github+json" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  /user/packages/maven/io.github.krm-demo.core-utils/versions \
  --jq '.')

# display the result of "gh api" call at standard output (optional and must be commented out after debugging!)
#echo $GH_PACKAGE_VERSIONS_ALL | jq

# saving the list of all versions of package "io.github.krm-demo.core-utils" package into JSON-file:
echo $GH_PACKAGE_VERSIONS_ALL | jq > .github/th-vars/var-ghPkgVer.json

# process the th-template to print the markdown-content in standard output:
.github/th-tool.sh process .github/th-templates/GH-Packages-Versions-All.md.th > $GITHUB_STEP_SUMMARY
