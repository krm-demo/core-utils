#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#  Signing the file, whose path is the forst argument, with GPG and create a detached signature-file (*.asc)
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

# Perform the signing with a specified passphrase
echo "1qaz@WSX0okm(IJN" | gpg --batch --yes --pinentry-mode loopback --passphrase-fd 0 \
  --local-user 0436EE146A372544 \
  --output $1.asc \
  --detach-sig --sign $1