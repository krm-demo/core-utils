#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Signing the file, whose path is the first argument, with GPG and create a detached signature-file (*.asc)
#  - this bash-script is passing passphrase from a separate file;
# ----------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

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
echo "1qaz@WSX0okm(IJN" > passphrase.txt

# Perform the signing with a specified passphrase
gpg --batch --yes --pinentry-mode loopback --passphrase-file passphrase.txt \
  --local-user 0436EE146A372544 \
  --output $1.asc \
  --detach-sig --sign $1