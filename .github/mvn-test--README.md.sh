#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Run the test, which executes "th-tool" template './github/th-templates/ROOT-Readme.md.th'
# -------------------------------------------------------------------------------------------------------

# Note! For maven-failsafe-plugin the system-property should be "it.test", but not just "test" !!!
mvn clean package "-DskipTests" && mvn failsafe:integration-test "-Dit.test=ThymeleafToolTest#testProcess_Readme"

