#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
# A test bash-script to play with extension and mime-type of a file, whose path is the first argument
# ----------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- path to check is '$1'"

FILE_EXTENSION=$(echo ${1##*.} | tr '[:upper:]' '[:lower:]')
FILE_MIME_TYPE=$(file -b --mime-type $1)
echo "- FILE_EXTENSION is '${FILE_EXTENSION}'"
echo "- FILE_MIME_TYPE is '${FILE_MIME_TYPE}'"

FILE_EXT_REGEX='^(asc|sig)$'
if [[ $FILE_EXTENSION =~ $FILE_EXT_REGEX ]]; then
  echo "- the file has one of the expected extension - /$FILE_EXT_REGEX/"
else
  echo "- the file does NOT have the expected extension - /$FILE_EXT_REGEX/"
fi

MIME_TYPE_REGEX="text.*|.*/pgp.*"
if [[ $FILE_MIME_TYPE =~ $MIME_TYPE_REGEX ]]; then
  echo "- the file mime-type is one of the expected - /$MIME_TYPE_REGEX/"
else
  echo "- the file mime-type is NOT one of the expected - /$MIME_TYPE_REGEX/"
fi

echo ".... finish the script $0 in '$(pwd)' ...."
