#!/usr/bin/env bash
# ----------------------------------------------------------------------------------------------------------------
#   Run the test, which performs the processing of  the directory '.github/th-test-site/original' with "th-tool"
# ----------------------------------------------------------------------------------------------------------------

mvn clean package "-DskipTests" && mvn test "-Dtest=ThymeleafToolTest#testProcessDir_JavaDoc"
