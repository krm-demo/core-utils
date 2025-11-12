#!/usr/bin/env bash
# ---------------------------------------------------------------------------------------------------------------
#   This bash-script sends an HTTP-request of method-POST to 'https://central.sonatype.com' to deploy a bundle:
# - see: https://central.sonatype.org/publish/publish-portal-api/#uploading-a-deployment-bundle
# - see also: https://central.sonatype.com/api-doc
# - ...
# ---------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

# TODO: move the following variable to GitHub-secrets !!!
BUNDLE_FILE=$(find ./target -name *.zip)
MC_USER_TOKEN="eWVyQnF0OmNmdTN4cUZWVnVzUlhWWThXc1lWa2RiVk5TQUFwR3NIcg=="
echo "- BUNDLE_FILE = '$BUNDLE_FILE'"
echo "- MC_USER_TOKEN = 'MC_USER_TOKEN'"
echo "... going to invoke '/publisher/uplopad' endpoint ..."

MC_DEPLOYMENT_ID=$(curl -s \
  -X 'POST' \
  -H 'accept: text/plain' \
  -H 'Content-Type: multipart/form-data' \
  -H "Authorization: Bearer ${MC_USER_TOKEN}" \
  -F "bundle=@${BUNDLE_FILE};type=application/zip" \
  'https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC')
echo "- MC_DEPLOYMENT_ID = '${MC_DEPLOYMENT_ID}';"

PAUSE_SECONDS=5
echo "- pause on ${PAUSE_SECONDS} seconds;"
sleep ${PAUSE_SECONDS}
echo "... going to invoke '/publisher/status' endpoint ..."

MC_DEPLOYMENT_STATUS=$(curl -s \
  -X 'POST' \
  -H 'accept: application/json' \
  -H "Authorization: Bearer ${MC_USER_TOKEN}" \
  "https://central.sonatype.com/api/v1/publisher/status?id=${MC_DEPLOYMENT_ID}")
echo "---------------- MC_DEPLOYMENT_STATUS: ----------------"
echo ${MC_DEPLOYMENT_STATUS} | jq
echo "-------------------------------------------------------"

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  GITHUB_STEP_SUMMARY=$(dirname $0)/Maven-Central--Deploymemnt-Status.md
  # for debug/test purposes the local summary is always cleared
  rm -rf $GITHUB_STEP_SUMMARY
fi
echo "- going to dump the deployment status into --> '$GITHUB_STEP_SUMMARY'..."

echo "<details><summary>Maven-Central Deployment-Status #<code>${MC_DEPLOYMENT_ID}</code></summary>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY
echo "\`\`\`json" >> $GITHUB_STEP_SUMMARY
echo ${MC_DEPLOYMENT_STATUS} | jq >> $GITHUB_STEP_SUMMARY
echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
echo "</details>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY

echo ".... finish the script $0 in '$(pwd)' ...."
