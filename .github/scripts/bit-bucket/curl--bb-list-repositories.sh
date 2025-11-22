#!/usr/bin/env bash
# -----------------------------------------------------------------------------------------------------------
#   A sample script to work with Bit-Bucket-API (loading the list repositories in a workspace)
# -----------------------------------------------------------------------------------------------------------
#echo "... starting the script $0 in '$(pwd)' ..."

# TODO: put it into GitHub-secrets
MY_BB_TOKEN=$(echo "6F240CE2=8izNQBZl4e2h0b-C6JmEsV2eQGZlLDKm9IVR25rp3jBQXEPmMJM95jH19DV8P24ThX0UKbPacFlrk6CLGVNkFddUEe4HBD9-pOfqw9-vOH1rvovxCGZnaB8hSpvXnbqBdbb2ft2MsudTKUFtHlx3AWJE2fa6lQ9z2hTtXhtIjHr0NGfFx3TTCTA" | rev)

curl -s --request GET \
  --url 'https://api.bitbucket.org/2.0/repositories/aleksey-kurmanov' \
  --header "Authorization: Bearer $MY_BB_TOKEN" \
  --header 'Accept: application/json' | jq
