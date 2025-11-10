#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Signing the file, whose path is the first argument, with GPG and create a detached signature-file (*.asc)
#  - this bash-script is passing the passphrase as an input from 'echo';
# ----------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- RUNNER_TEMP = '$RUNNER_TEMP';"
echo "- GPG_KEY_ID = '$GPG_KEY_ID';"
echo "- GPG_PASSPHRASE = '$GPG_PASSPHRASE';"
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

# Perform the signing with a specified passphrase
echo $GPG_PASSPHRASE | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 \
  --local-user $GPG_KEY_ID \
  --output $1.asc \
  --detach-sig --sign $1

echo ".... finish the script $0 in '$(pwd)' ...."
