#!/usr/bin/env bash
# ----------------------------------------------------------------------------------------------------------------
#   Run the test, which performs the processing of  the directory '.github/th-test-site/original' with "th-tool"
# ----------------------------------------------------------------------------------------------------------------

mvn test "-Dtest=ThymeleafToolTest#testProcessDir_TestSite"
