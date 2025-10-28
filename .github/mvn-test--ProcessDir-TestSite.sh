#!/usr/bin/env bash
# ----------------------------------------------------------------------------------------------------------------
#   Run the test, which performs the processing of  the directory '.github/th-test-site/original' with "th-tool"
# ----------------------------------------------------------------------------------------------------------------

# Note! For maven-failsafe-plugin the system-property should be "it.test", but not just "test" !!!
mvn clean package "-DskipTests" && mvn failsafe:integration-test "-Dit.test=ThymeleafToolTest#testProcessDir_TestSite"
