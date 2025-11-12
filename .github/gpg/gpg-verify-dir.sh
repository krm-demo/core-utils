#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Verifying the GPG-signature of all related files in the dirrectory, whose path is passed as the first
#  command-line argument. All found files are detached GPG-signature with extension '.asc' or '.sig'.
#  and the source-file (the target of signature) is expected to be a sibling in the same directory.
# - if the variable $GITHUB_STEP_SUMMARY exists - the file with that name is appended with GitHub-Markdown results;
# - if it variable $GITHUB_STEP_SUMMARY does not exists - it's assigned to the name similar to this bash-script;
# ----------------------------------------------------------------------------------------------------------
echo "=== starting the script $0 in '$(pwd)' ==="
echo "- directory to verify GPG-signatures is '$1';"
echo "- GITHUB_STEP_SUMMARY --> '$GITHUB_STEP_SUMMARY';"

FILE_EXTENSION=$(echo ${1##*.} | tr '[:upper:]' '[:lower:]')
FILE_MIME_TYPE=$(file -b --mime-type $1)
echo "- FILE_EXTENSION is '${FILE_EXTENSION}'"
echo "- FILE_MIME_TYPE is '${FILE_MIME_TYPE}'"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Error: No argument provided. Please specify a path to directory to verify GPG-signatures."
  exit -1
fi

# Check if the provided argument is an existing directory
if [ ! -d "$1" ]; then
  echo "Error: The directory '$1' does not exist or is not a directory."
  exit -2
fi

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  export GITHUB_STEP_SUMMARY=$(dirname $0)/GPG-Verify-Dir.md
  echo "GITHUB_STEP_SUMMARY is assigned to local file --> '$GITHUB_STEP_SUMMARY'"
  rm -rf $GITHUB_STEP_SUMMARY
fi
echo "going to dump the verification result into --> '$GITHUB_STEP_SUMMARY'..."

echo "## Verifying the GPG-signatures in directory \`$1\`" >> $GITHUB_STEP_SUMMARY
echo "- full directory path is \`$(readlink -f $1)\`" >> $GITHUB_STEP_SUMMARY
COUNT_GPG=$(find target/deploy-dir--maven-central/ \( -name '*.asc' -or -name '*.sig' \) | wc -l)
echo "- ${COUNT_GPG} files with GPG-signatures are located within target directory;"
echo -e "" >> $GITHUB_STEP_SUMMARY

# the statement below looks like list-comprehension in Python or Stream.forEach in Java:
find target/deploy-dir--maven-central/ \( -name '*.asc' -or -name '*.sig' \) -exec $(dirname $0)/gpg-verify.sh {} \;

echo "==== finish the script $0 in '$(pwd)' ===="
