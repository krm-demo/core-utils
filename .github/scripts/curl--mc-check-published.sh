#!/usr/bin/env bash
# ---------------------------------------------------------------------------------------------------------------
#   This bash-script sends an HTTP-request of method-POST to 'https://central.sonatype.com' to deploy a bundle:
# - see: https://central.sonatype.org/publish/publish-portal-api/#uploading-a-deployment-bundle
# - see also: https://central.sonatype.com/api-doc
# - ...
# ---------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

# TODO: move the following variable to GitHub-secrets !!!
MAVEN_CENTRAL_USER_TOKEN="eWVyQnF0OmNmdTN4cUZWVnVzUlhWWThXc1lWa2RiVk5TQUFwR3NIcg=="
echo "MAVEN_CENTRAL_USER_TOKEN = '$MAVEN_CENTRAL_USER_TOKEN'"

MAVEN_CENTRAL_NAMESPACE="io.github.krm-demo"
MAVEN_CENTRAL_COMPONENT_NAME="core-utils"
MAVEN_CENTRAL_COMPONENT_VERSION="21.28.001"

HTTP_URL_GET="https://central.sonatype.com/api/v1/publisher/published?namespace=${MAVEN_CENTRAL_NAMESPACE}"
HTTP_URL_GET="${HTTP_URL_GET}&name=${MAVEN_CENTRAL_COMPONENT_NAME}"
HTTP_URL_GET="${HTTP_URL_GET}&version=${MAVEN_CENTRAL_COMPONENT_VERSION}"
echo "HTTP_URL_GET = '${HTTP_URL_GET}'"

HTTP_RESPONSE=$(curl -s \
  -X 'GET' \
  -H 'accept: application/json' \
  -H "Authorization: Bearer ${MAVEN_CENTRAL_USER_TOKEN}" \
  $HTTP_URL_GET)

echo $HTTP_RESPONSE | jq

echo ".... finish the script $0 in '$(pwd)' ...."
