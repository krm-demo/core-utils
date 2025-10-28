#!/usr/bin/env bash
# ----------------------------------------------------------------------------------------------------------------
#   Build and install the project and run integration (post-install and post-verify) tests after that
# ----------------------------------------------------------------------------------------------------------------

# Note! It's important to execute 2 separate maven-reactors, because "failsafe" is not inside the default lifecycle
mvn clean install && mvn failsafe:integration-test
