#!/usr/bin/env bash
# --------------------------------------------------------------------------------------------------------------
#        Import the GPG-keys from GitHub-secrets (could not be executed, but only copy/pasted into workflow):
# --------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

# save a passphrase in a text-file:
echo "1qaz@WSX0okm(IJN" > passphrase.txt

# Import GPG-keys from GitHub-secrets
echo -n "${{ secrets.GPG_SIGNING_KEY }}" | base64 --decode | gpg --batch --import --passphrase-file passphrase.txt
