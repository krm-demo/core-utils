[![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/krm-demo/core-utils)

[![Java-Doc](https://img.shields.io/badge/GH--Pages-core--utils.x.y.z-blue)](https://krm-demo.github.io/core-utils/)
[![Java-Doc](https://img.shields.io/badge/GH--Pages-core--utils.x.y.z.SNAPSHOT-blue)](https://krm-demo.github.io/core-utils/)

# core-utils
Utility-classes to simplify working with core-java API (streams, collections, input-output)

### Read-Only access to the current GitHUb-Packages maven-repository
In order to be possible to use the remnote maven-repository that is bound
to [GitHUb-Packages](https://docs.github.com/en/packages) of this [krm-demo/core-utils](https://github.com/krm-demo/core-utils)
GitHub-repository - you need to update your home maven-settings XML-file, which is usually located at `~/.m2/settings.xml`,
in approximately following way:
```XML
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
  <servers>
    . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
    <server>
      <id>github</id>
      <username>krm-demo</username>
      <!-- following token allows read-only access to 'krm-demo' account: -->
      <password>*** see the instruction below ****</password>
    </server>
    . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
  </servers>

</settings>
```
The token above is read-only and does not have expiration - so, no need to keep it confidentially...
The password is not allowed to be published (becuase in general it could give you the full access), -
so, the content of `<password>`-tag above is:
- prefix: `ghp_`
- mid-part: `L1Eiw9V4CXLZVl0nu2QkqWDVpQE4zl0k`
- suffix: `cYz6`
