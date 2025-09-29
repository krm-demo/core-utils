#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------
#  This bash-script scans and saves the content of the current folder (the parent of this script)
#  into the summary of git-hub workflow action's step (into the file "$GITHUB_STEP_SUMMARY")
# -------------------------------------------------------------------------------------------------

if [[ -z ${GITHUB_STEP_SUMMARY+x} ]]; then
  GITHUB_STEP_SUMMARY=$(dirname $0)/summary-sections.md
  # for debug/test purposes the local summary is always cleared
  rm -rf $GITHUB_STEP_SUMMARY
fi
echo "going to dump JSON-files into '$GITHUB_STEP_SUMMARY':"

for json_file in $(dirname $0)/*.json; do
  if [ -f "$json_file" ]; then
    echo "$(basename $json_file) --> '$json_file'"
    echo "<details><summary>\`$json_file\`</summary>" >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
    echo "\`\`\`json" >> $GITHUB_STEP_SUMMARY
    cat $json_file >> $GITHUB_STEP_SUMMARY
    echo "\`\`\`" >> $GITHUB_STEP_SUMMARY
    echo "</details>" >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
  fi
done

echo "... ... ... ... ... ... ... ... ... ..."
echo "the content of file '$GITHUB_STEP_SUMMARY':"
echo "... ... ... ... ... ... ... ... ... ..."
cat $GITHUB_STEP_SUMMARY