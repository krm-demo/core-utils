#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Signing the file, whose path is the forst argument, with GPG and create a detached signature-file (*.asc)
#  - this bash-script is passing the passphrase as an input from 'echo';
# ----------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "GPG-file (extension '.asc') to verify is '$1'"
echo "GITHUB_STEP_SUMMARY --> '$GITHUB_STEP_SUMMARY'"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Error: No argument provided. Please specify a '.asc'-file to verify GPG-signature."
  exit 1
fi

# Check if the provided argument is an existing regular file
if [ ! -f "$1" ]; then
  echo "Error: File '$1' does not exist or is not a regular file."
  exit 1
fi

echo "------------ gpg --verify '$1': ---------------"
gpg --verify $1
echo "---------------------------------------------$(echo $1 | tr '[:graph:]' '-')"

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  GITHUB_STEP_SUMMARY=$(dirname $1)/gpg-verify--$(basename $1).md
  echo "GITHUB_STEP_SUMMARY is assigned to local file --> '$GITHUB_STEP_SUMMARY'"
fi
echo "going to dump the verification result into --> '$GITHUB_STEP_SUMMARY'..."

echo "### Verifying the GPG-file \`...$(basename $1)\`" >> $GITHUB_STEP_SUMMARY
echo -e "" >> $GITHUB_STEP_SUMMARY

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
