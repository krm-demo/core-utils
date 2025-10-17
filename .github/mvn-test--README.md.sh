#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Run the test, which executes "th-tool" template './github/th-templates/ROOT-Readme.md.th'
# -------------------------------------------------------------------------------------------------------

mvn test "-Dtest=ThymeleafToolTest#testProcess_Readme"
