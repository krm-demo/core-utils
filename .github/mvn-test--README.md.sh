#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Run the test, which executes "th-tool" template './github/th-templates/ROOT-Readme.md.th'
# -------------------------------------------------------------------------------------------------------

# deleting the logs
rm -rf target/logs/th-tool.log

# Note! For maven-failsafe-plugin the system-property should be "it.test", but not just "test" !!!
mvn clean package "-DskipTests" && mvn failsafe:integration-test "-Dit.test=ThymeleafToolTest#testProcess_Readme"

# see the output in 'th-tool.log'-file
cat target/logs/th-tool.log