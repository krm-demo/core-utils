#!/usr/bin/env bash
# ------------------------------------------------------------------------------------------------------------------
#   Deploying the artifacts to local nexus-repository (which is the same as remote one, but hosted locally)
# ------------------------------------------------------------------------------------------------------------------
echo "... starting the script $0 in '$(pwd)' ..."

TARGET_DIR="./target"
JAR_FILE__BIN=$(find ${TARGET_DIR} -name "core*.jar" ! -name "*javadoc.jar" ! -name "*sources.jar" -exec basename {} \;)
JAR_FILE__SOURCES="${JAR_FILE__BIN%.*}-sources.jar"
JAR_FILE__JAVADOC="${JAR_FILE__BIN%.*}-javadoc.jar"
JAR_FILE__JAVADOC_BACKUP="${JAR_FILE__BIN%.*}-javadoc-backup.jar"

echo "- JAR_FILE__BIN = '${JAR_FILE__BIN}'"
echo "- JAR_FILE__SOURCES = '${JAR_FILE__SOURCES}'"
echo "- JAR_FILE__JAVADOC = '${JAR_FILE__JAVADOC}'"
echo "- JAR_FILE__JAVADOC_BACKUP = '${JAR_FILE__JAVADOC_BACKUP}'"

echo "- renaming the existing javadoc-file for backup and make the processed JavaDoc with that name:"
mv ${TARGET_DIR}/${JAR_FILE__JAVADOC} ${TARGET_DIR}/${JAR_FILE__JAVADOC_BACKUP}
jar cvf ${TARGET_DIR}/${JAR_FILE__JAVADOC} -C ${TARGET_DIR}/reports/apidocs-processed .

echo "- list of target JARs are:"
ls -laxo ${TARGET_DIR}/*.jar

#echo "- the content of old JavaDoc is"
#jar tfv ${TARGET_DIR}/${JAR_FILE__JAVADOC_BACKUP}
#echo "- the content of new (processed) JavaDoc is"
#jar tfv ${TARGET_DIR}/${JAR_FILE__JAVADOC}

echo "- deploying to local-nexus with 'mvn -X deploy:deploy-file ...' command:"
mvn -X deploy:deploy-file -DgroupId="io.github.krm-demo" \
  -DartifactId="core-utils" \
  -Dversion="21.24.001-SNAPSHOT" \
  -Dpackaging="jar" \
  -Dfile="${TARGET_DIR}/${JAR_FILE__BIN}" \
  -Dfiles="${TARGET_DIR}/${JAR_FILE__SOURCES},${TARGET_DIR}/${JAR_FILE__JAVADOC}" \
  -Dclassifiers="sources,javadoc" \
  -Dtypes="jar,jar" \
  -DrepositoryId="github" \
  -Durl=https://maven.pkg.github.com/krm-demo/core-utils
