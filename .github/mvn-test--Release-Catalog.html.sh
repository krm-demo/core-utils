#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Run the test, which executes "th-tool" template './github/th-templates/GH-PAGES--Release-Catalog.html.th'
# -------------------------------------------------------------------------------------------------------

mvn test "-Dtest=ThymeleafToolTest#testProcess_ReleaseCatalog"
