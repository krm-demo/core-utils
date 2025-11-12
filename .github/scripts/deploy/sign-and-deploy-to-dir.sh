#!/usr/bin/env bash
# --------------------------------------------------------------------------------------------
#       Signing with GPG and deploying to the local directory for test/debug purposes
# ---------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- GPG_KEY_ID = '$GPG_KEY_ID';"
echo "- GPG_PASSPHRASE = '$GPG_PASSPHRASE';"
echo "- content of 'passphrase.txt' --> '$(cat passphrase.txt 2>/dev/null || echo "<< does not exists >>")';"

source "$(dirname $0)/pom-props.source"

echo "- JAR_FILE__BIN = '${JAR_FILE__BIN}'"
echo "- JAR_FILE__SOURCES = '${JAR_FILE__SOURCES}'"
echo "- JAR_FILE__JAVADOC = '${JAR_FILE__JAVADOC}'"
echo "- JAR_FILE__JAVADOC_BACKUP = '${JAR_FILE__JAVADOC_BACKUP}'"

echo "- POM_PROPS_GROUPID = '${POM_PROPS_GROUPID}'"
echo "- POM_PROPS_ARTIFACTID = '${POM_PROPS_ARTIFACTID}'"
echo "- POM_PROPS_VERSION = '${POM_PROPS_VERSION}'"

echo "- renaming the existing javadoc-file for backup and create a JAR-file with that name from the processed JavaDoc:"
mv ${JAR_FILE__JAVADOC} ${JAR_FILE__JAVADOC_BACKUP}
jar cvf ${JAR_FILE__JAVADOC} -C target/reports/apidocs-processed .

echo "- list of target JARs are:"
ls -laxo target/*.jar

#echo "- the content of old JavaDoc is"
#jar tfv ${JAR_FILE__JAVADOC_BACKUP}
#echo "- the content of new (processed) JavaDoc is"
#jar tfv ${JAR_FILE__JAVADOC}

echo "- deploying to 'GitHub Packages' with 'mvn -X gpg:sign-and-deploy-file ...' command:"
# TODO: the command above could be simplified using maven MOJO like "...> mvn gpg:sign-and-deploy-file@local-nexus"
mvn -B gpg:sign-and-deploy-file \
  -DgroupId="${POM_PROPS_GROUPID}" \
  -DartifactId="${POM_PROPS_ARTIFACTID}" \
  -Dversion="${POM_PROPS_VERSION}" \
  -Dpackaging="jar" \
  -Dfile="${JAR_FILE__BIN}" \
  -Dfiles="${JAR_FILE__SOURCES},${JAR_FILE__JAVADOC}" \
  -Dclassifiers="sources,javadoc" \
  -Dtypes="jar,jar" \
  -DpomFile="pom.xml" \
  -Dgpg.keyname="$GPG_KEY_ID" \
  -Dgpg.passphrase="$GPG_PASSPHRASE" \
  -DrepositoryId="local-nexus" \
  -Durl=file:///$(pwd)/target/deploy-dir--maven-central

#echo "- hierarchy of deploying bundle:"
#tree -s ./target/deploy-dir--maven-central

(cd target/deploy-dir--maven-central/ && zip -q -r ${JAR_FILE__BIN%.*}.zip .)

echo "- hierarchy of bundle-ZIP to deploy:"
unzip -l ${JAR_FILE__BIN%.*}.zip
