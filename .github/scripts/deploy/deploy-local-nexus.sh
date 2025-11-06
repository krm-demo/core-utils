#!/usr/bin/env bash
# ------------------------------------------------------------------------------------------------------------------
#   Deploying the artifacts to local nexus-repository (which is the same as remote one, but hosted locally)
# ------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

mvn deploy:deploy-file -DgroupId="io.github.krm-demo" \
  -DartifactId="core-utils" \
  -Dversion="21.23.008-SNAPSHOT" \
  -Dpackaging="jar" \
  -Dfile="./target/core-utils-21.23.008-SNAPSHOT-sources.jar" \
  -DrepositoryId="local-nexus" \
  -Durl=http://localhost:8081/nexus/
