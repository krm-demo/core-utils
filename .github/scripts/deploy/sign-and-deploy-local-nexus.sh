#!/usr/bin/env bash
# ------------------------------------------------------------------------------------------------------------------
#   Deploying the artifacts to local nexus-repository (which is the same as remote one, but hosted locally)
# ------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

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

echo "- the content of old JavaDoc is"
jar tfv ${JAR_FILE__JAVADOC_BACKUP}
echo "- the content of new (processed) JavaDoc is"
jar tfv ${JAR_FILE__JAVADOC}

echo "- deploying to local-nexus with 'mvn -X deploy:deploy-file ...' command:"
mvn -X deploy:deploy-file \
  -DgroupId="${POM_PROPS_GROUPID}" \
  -DartifactId="${POM_PROPS_ARTIFACTID}" \
  -Dversion="${POM_PROPS_VERSION}" \
  -Dpackaging="jar" \
  -Dfile="${JAR_FILE__BIN}" \
  -Dfiles="${JAR_FILE__SOURCES},${JAR_FILE__JAVADOC}" \
  -Dclassifiers="sources,javadoc" \
  -Dtypes="jar,jar" \
  -DrepositoryId="local-nexus" \
  -Durl=http://localhost:8081/repository/maven-snapshots/

# TODO: the command above could be simplified using maven MOJO like "...> mvn deploy:deploy-file@local-nexus"
