#!/usr/bin/env bash
# --------------------------------------------------------------------------------------------------------------
#     Import the GPG-keys from GitHub-secrets (should be executed from GitHub-workflow):
# - env-var '$GPG_PASSPHRASE' (a GitHub-secret '${{ secrets.GPG_PASSPHRASE }}'),
#   which must contain GOG-passphrase;
# - env-var '$GPG_SIGNING_KEY' (a GitHub-secret '${{ secrets.GPG_SIGNING_KEY }}),
#   which must contain a result of `...>gpg --export-secret-keys 0436EE146A372544 | base64`,
#   where '0436EE146A372544' is the value of env-var '$GPG_KEY_ID' (a GitHUb-repo-var '${{ vars.GPG_KEY_ID }}');
# --------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- GPG_KEY_ID = '$GPG_KEY_ID'"

echo "- saving GPG-passphrase to './passphrase.txt' ..."
echo $GPG_PASSPHRASE > passphrase.txt

echo "-------  gpg --import ...  -----------------"
echo $GPG_SIGNING_KEY | base64 --decode | gpg --batch --import --passphrase-file passphrase.txt

echo "-------  gpg --edit-key ...  ---------------"
echo -e "5\ny\nquit" | gpg --batch --yes --pinentry-mode loopback --command-fd 0 --expert --edit-key 0436EE146A372544 trust

echo "-------  gpg --update-trustdb ...  ---------"
gpg --update-trustdb

echo ".... finish the script $0 in '$(pwd)' ...."
