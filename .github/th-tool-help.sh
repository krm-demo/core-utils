#!/usr/bin/env bash
# -------------------------------------------------------------------------
#   Build and execute 'th-tool' to output the help-message into console.
# -------------------------------------------------------------------------

mvn test-compile exec:java@th-tool -Dexec.args="--help"
echo "--------------------------------------------------------"
mvn test-compile exec:java@th-tool -Dexec.args="eval --help"
echo "--------------------------------------------------------"
mvn test-compile exec:java@th-tool -Dexec.args="process --help"
echo "--------------------------------------------------------"
