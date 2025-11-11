#!/usr/bin/env bash
# --------------------------------------------------------------------------------------------
#   Signing with GPG and deploying the target JAR-files to "GitHub Packages"
#         (which is used for INTERNAL-releases in this project)
# - this script is the same as "deploy-github-packages.sh", but uses different maven-plugin;
# - when executing at GitHub it's very import to import GPG-keys with "gpg-import.sh" before;
# ---------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."
echo "- GPG_KEY_ID = '$GPG_KEY_ID';"
echo "- GPG_PASSPHRASE = '$GPG_PASSPHRASE';"
echo "- MAVEN_GPG_PASSPHRASE = '$MAVEN_GPG_PASSPHRASE';"
echo "- content of 'passphrase.txt' --> '$(cat passphrase.txt 2>/dev/null || echo "<< does not exists >>")';"

if [[ "$GPG_PASSPHRASE" == "$MAVEN_GPG_PASSPHRASE" ]]; then
  echo "- variables GPG_PASSPHRASE equals to MAVEN_GPG_PASSPHRASE"
else
  echo "- variables GPG_PASSPHRASE does NOT equal to MAVEN_GPG_PASSPHRASE "'!!!'
fi

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

# - env-var with predefined name 'MAVEN_GPG_PASSPHRASE' is used to provide the parameter "-Dgpg.passphrase";
# - as for parameter "-Dgpg.keyname" - it looks like env-var MAVEN_GPG_KEY is not working in our case;

echo "- deploying to 'GitHub Packages' with 'mvn -X gpg:sign-and-deploy-file ...' command:"
# TODO: the command above could be simplified using maven MOJO like "...> mvn gpg:sign-and-deploy-file@github"
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
  -DrepositoryId="github" \
  -Durl=https://maven.pkg.github.com/krm-demo/core-utils

EXIT_CODE__SIGN_AND_DEPLOY=$?
if [ $? -eq 0 ]; then
    echo "> [!NOTE]" >> $GITHUB_STEP_SUMMARY
    echo "> It looks like deployment to **GitHub-Packages** was successful"'!!!' >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
else
    echo "> [!CAUTION]" >> $GITHUB_STEP_SUMMARY
    echo "> EXIT_CODE of \`mvn gpg:sign-and-deploy-file ...\` is $EXIT_CODE__SIGN_AND_DEPLOY" >> $GITHUB_STEP_SUMMARY
    echo "> There was an **error** during deployment to **GitHub-Packages** - check the logs for details" >> $GITHUB_STEP_SUMMARY
    echo -e "" >> $GITHUB_STEP_SUMMARY
fi

.github/gpg/gpg-verify.sh ${JAR_FILE__BIN}.asc
.github/gpg/gpg-verify.sh ${JAR_FILE__SOURCES}.asc
.github/gpg/gpg-verify.sh ${JAR_FILE__JAVADOC}.asc