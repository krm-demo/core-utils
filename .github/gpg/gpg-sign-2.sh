#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Signing the file, whose path is the first argument, with GPG and create a detached signature-file (*.asc)
#  - this bash-script is passing passphrase from a separate file;
# ----------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- RUNNER_TEMP = '$RUNNER_TEMP';"
echo "- GPG_KEY_ID = '$GPG_KEY_ID';"
echo "- content of 'passphrase.txt' --> '$(cat passphrase.txt 2>/dev/null || echo "<< does not exists >>")';"

# Check if the first argument is provided
if [ -z "$1" ]; then
  echo "Error: No argument provided. Please specify a file."
  exit 1
fi

# Check if the provided argument is an existing regular file
if [ ! -f "$1" ]; then
  echo "Error: File '$1' does not exist or is not a regular file."
  exit 1
fi

# save a passphrase in a text-file:
echo $GPG_PASSPHRASE > passphrase.txt

# Perform the signing with a specified passphrase
gpg --batch --yes --pinentry-mode loopback --passphrase-file passphrase.txt \
  --local-user $GPG_KEY_ID \
  --output $1.asc \
  --detach-sig --sign $1