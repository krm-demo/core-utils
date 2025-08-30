#!/usr/bin/env bash
# -------------------------------------------------------------------------------------------------------
#   Execute (not build!) 'th-tool' and delegate all command-line of this bash-script's arguments to it.
# -------------------------------------------------------------------------------------------------------

mvn --quite exec:java@th-tool -Dexec.args="$*"