#!/usr/bin/env bash
# --------------------------------------------------------------------------------------------
#   Signing with GPG and deploying the target JAR-files to "Maven-Central" repository
#         (which is the same as remote one, but hosted locally)
# - this script is the same as "deploy-local-nexus.sh", but uses different maven-plugin;
# - when executing at GitHub it's very import to import GPG-keys with "gpg-import.sh" before;
# ---------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- GPG_KEY_ID = '$GPG_KEY_ID';"
echo "- GPG_PASSPHRASE = '$GPG_PASSPHRASE';"
echo "- content of 'passphrase.txt' --> '$(cat passphrase.txt 2>/dev/null || echo "<< does not exists >>")';"
echo "- reversed MAVEN__CENTRAL_USERNAME = '$(echo $MAVEN__CENTRAL_USERNAME | rev)';"
echo "- reversed MAVEN__CENTRAL_PASSWORD = '$(echo $MAVEN__CENTRAL_PASSWORD | rev)';"

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
# TODO: the command below could be simplified using maven MOJO like "...> mvn gpg:sign-and-deploy-file@maven-central"
mvn -B gpg:sign-and-deploy-file \
  -DgroupId="${POM_PROPS_GROUPID}" \
  -DartifactId="${POM_PROPS_ARTIFACTID}" \
  -Dversion="${POM_PROPS_VERSION}" \
  -Dpackaging="jar" \
  -Dfile="${JAR_FILE__BIN}" \
  -Dfiles="${JAR_FILE__SOURCES},${JAR_FILE__JAVADOC}" \
  -Dclassifiers="sources,javadoc" \
  -Dtypes="jar,jar" \
  -Dgpg.keyname="$GPG_KEY_ID" \
  -Dgpg.passphrase="$GPG_PASSPHRASE" \
  -DrepositoryId="maven-central" \
  -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/

EXIT_CODE__SIGN_AND_DEPLOY=$?
if [ EXIT_CODE__SIGN_AND_DEPLOY -eq 0 ]; then
    echo "> [!NOTE]" >> $GITHUB_STEP_SUMMARY
    echo "> It looks like deployment to [**Maven-Central**](https://central.sonatype.com/publishing/deployments) was successful"'!!!' >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
else
    echo "> [!CAUTION]" >> $GITHUB_STEP_SUMMARY
    echo "> EXIT_CODE of \`mvn gpg:sign-and-deploy-file ...\` is $EXIT_CODE__SIGN_AND_DEPLOY" >> $GITHUB_STEP_SUMMARY
    echo "> There was an error during deployment to **Maven-Central** - check the logs for details" >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
fi

.github/gpg/gpg-verify.sh ${JAR_FILE__BIN}.asc
.github/gpg/gpg-verify.sh ${JAR_FILE__SOURCES}.asc
.github/gpg/gpg-verify.sh ${JAR_FILE__JAVADOC}.asc
