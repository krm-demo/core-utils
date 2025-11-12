#!/usr/bin/env bash
# ---------------------------------------------------------------------------------------------------------------------
#  Verifying the GPG-signature file, whose path is passed as the first command-line argument.
#  The passed file is expected to be of detached GPG-signature with extension '.asc' or '.sig'
#  and the source-file (the target of signature) is expected to be a sibling in the same directory.
# - if the variable $GITHUB_STEP_SUMMARY exists - the file with that name is appended with GitHub-Markdown results;
# - if it variable $GITHUB_STEP_SUMMARY does not exists - it's assigned with the name of target-file followed by '.md'
# ---------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- GPG-file (expected extension '.asc' or '.sig') to verify is '$1';"
echo "- GITHUB_STEP_SUMMARY --> '$GITHUB_STEP_SUMMARY';"

FILE_EXTENSION=$(echo ${1##*.} | tr '[:upper:]' '[:lower:]')
FILE_MIME_TYPE=$(file -b --mime-type $1)
echo "- FILE_EXTENSION is '${FILE_EXTENSION}'"
echo "- FILE_MIME_TYPE is '${FILE_MIME_TYPE}'"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Error: No argument provided. Please specify a '.asc'-file to verify GPG-signature."
  exit 1
fi

# Check if the provided argument is an existing regular file
if [ ! -f "$1" ]; then
  echo "Error: File '$1' does not exist or is not a regular file."
  exit 2
fi

# Check the extension of a provided file
FILE_EXT_REGEX='^(asc|sig)$'
if [[ ! $FILE_EXTENSION =~ $FILE_EXT_REGEX ]]; then
  echo "Error: File '$1' does not have an expected extension"
  exit 3
fi

echo "------------ gpg --verify '$1': ---------------"
gpg --verify $1
echo "---------------------------------------------$(echo $1 | tr '[:graph:]' '-')"

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  GITHUB_STEP_SUMMARY=$(dirname $1)/gpg-verify--$(basename $1).md
  echo "GITHUB_STEP_SUMMARY is assigned to local file --> '$GITHUB_STEP_SUMMARY'"
  rm -rf $GITHUB_STEP_SUMMARY
fi
echo "going to dump the verification result into --> '$GITHUB_STEP_SUMMARY'..."

echo "### Verifying the GPG-signature file \`...$(basename $1)\`" >> $GITHUB_STEP_SUMMARY
echo "- full file path is \`$(readlink -f $1)\`" >> $GITHUB_STEP_SUMMARY
echo "- file extension is \`$FILE_EXTENSION\`" >> $GITHUB_STEP_SUMMARY
echo "- file mime-type is \`$FILE_MIME_TYPE\`" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY

MIME_TYPE_REGEX="text.*|.*/pgp.*"
if [[ $FILE_MIME_TYPE =~ $MIME_TYPE_REGEX ]]; then
  echo "- the file mime-type is OK to be printed"
  echo "<details><summary>content of <code>...$(basename $1)</code></summary>" >> $GITHUB_STEP_SUMMARY
  echo -e "" >> $GITHUB_STEP_SUMMARY
  echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
  cat $1 >> $GITHUB_STEP_SUMMARY
  echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
  echo "</details>" >> $GITHUB_STEP_SUMMARY
  echo -e "" >> $GITHUB_STEP_SUMMARY
else
  echo "- the file mime-type is NOT printable"
fi

# Note! that 'gpg'-utility print the results to both standard system-streams - so, we have to redirect them properly !
echo "<details><summary><code>gpg --verify ...$(basename $1)</code></summary>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY
echo "\`\`\`bash" >> $GITHUB_STEP_SUMMARY
gpg --verify $1 &>> $GITHUB_STEP_SUMMARY
echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
echo "</details>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY

echo "<details><summary><code>gpg --status-fd 1 --verify ...$(basename $1)</code></summary>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY
echo "\`\`\`bash" >> $GITHUB_STEP_SUMMARY
gpg --status-fd 1 --verify $1 >> $GITHUB_STEP_SUMMARY
echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
echo "</details>" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY

echo ".... finish the script $0 in '$(pwd)' ...."
