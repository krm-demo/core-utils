#!/usr/bin/env bash
echo "... starting the script $0 in '$(pwd)' ..."
echo "--------- env-vars before executing the maven: -----------"
env | sort
echo "----------------------------------------------------------"

echo "
###Some test-summary of a workflow
| Command | Description |
| --- | --- |
| \`git status\` | List all *new or modified* files |
| \`git diff\` | Show file differences that **haven't been** staged |

To use in maven script:
\`\`\`XML
<dependency>
 <groupId>io.github.krm-demo</groupId>
 <artifactId>core-utils</artifactId>
 <version>21.0.2</version>
</dependency>
\`\`\`" >> $GITHUB_STEP_SUMMARY

