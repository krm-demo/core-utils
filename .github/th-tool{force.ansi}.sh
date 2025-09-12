#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Execute (not build!) 'th-tool' and delegate all command-line of this bash-script's arguments to it.
# -------------------------------------------------------------------------------------------------------

mvn --quiet test-compile exec:java@th-tool{force.ansi} -Dexec.args="$*"