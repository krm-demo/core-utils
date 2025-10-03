[![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg?event=push)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/krm-demo/core-utils)

[![Release-Catalog](https://img.shields.io/badge/GH--Pages-Release_Catalog-blue)](https://krm-demo.github.io/core-utils/)
[![Project-Site](https://img.shields.io/badge/GH--Pages-core--utils:21.09.001-blue)](https://krm-demo.github.io/core-utils/core-utils-21.09.001)

# <u>core-utils</u>

This Java-library contains utility-classes to simplify working with core-java API (streams, collections, io, ...).
It was initially created as a core and reusable part of **`th-tool`** (which is not published yet).
Some features and approaches could be very useful and helpful independently as a separate Java-library
that could be easily integrated into any Java-project as a maven/gradle artifact or as a dependency to jbang-script.

Full documentation (including JavDoc) and examples fort this particular version are available [here](https://krm-demo.github.io/core-utils/core-utils-21.09.001).
Other versions of ths project are listed in the [release catalog](https://krm-demo.github.io/core-utils/),
but the latest relevant versions (with relation to the current one) are:

> [!TIP]
> TODO: to be inserted the header section of [Release-Catalog](https://krm-demo.github.io/core-utils/)

---

This particular version of the project `21.09.001` is an _INTERNAL_-release version, which consists of
- major version `21` (that corresponds to minimum available version of JDK);
- minor version `9` (the ordinal number of _PUBLIC_-release);
- incremental version  `1` (the ordinal number of _INTERNAL_-release after the latest _PUBLIC_-release);

> [!IMPORTANT]
> It's highly recommended to use the latest _PUBLIC_-release version, because all others are either outdated or not stable yet

## Installation and Usage of INTERNAL-release of `core-utils`-library

> [!NOTE]
> The _INTERNAL_-release version has 3 integer parts (_major_, _minor_ and _incremental_) parts **without** suffix `-SNAPSHOT`.
> It's built and deployed into [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages) 
> artifatcory as a result of manual execution of GitHub workflow [`release-internal`](https://github.com/krm-demo/core-utils/actions/workflows/release-internal.yml).

The main purpose of _INTERNAL_-release is the final verification before _PUBLIC_-release 
and in addition to that it's the only true-way to verify the generation of `*.md`-files
from corresponding `th-tool` templates (they have `*.md.th` extensiom) via GitHub workflows.

Anyway it's possible to play with _INTERNAL_-release binaries, but in order to do that
some minor changes should be done in local [`~/.m2/maven-settings`-file](https://maven.apache.org/settings.html).

### Use [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages) as an additional remote maven-repository
Each GitHub account has its onw artifactory, which is known as [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages).
The most important thing for us is that artifactory could be used as an additional [remote maven repository](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry).
In order to refer this particular project - we must add following XML-tags into the local file [`~/.m2/maven-settings`-file](https://maven.apache.org/settings.html)
(or create such file in proper place if it was not created before):

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo.maven.apache.org/maven2</url>
        </repository>
        <repository>
          <id>github-krm-demo</id>
          <name>GitHub Packages of 'krm-demo/*' repositories</name>
          <url>https://maven.pkg.github.com/krm-demo/*</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  
  <servers>
    <server>
      <id>github-krm-demo</id>
      <username>OWNER_NAME</username>
      <password>MY_READONLY_TOKEN</password>
    </server>
  </servers>
</settings>
```
- where the value of `OWNER_NAME` should be <code>krm-demo</code> (you can try another `<username>`)
- and the value of `MY_READONLY_TOKEN` should be <code>ghp_&#8203;L1Eiw9V4CXLZVl0nu2QkqWDVpQE4zl0k&#8203;cYz6</code>
- (should be re-masked!) the value of `MY_ADMIN_TOKEN` should be <code>ghp_&#8203;UIEgLpCQoqASXtbxGrbpgfPXNYC94T19&#8203;NqXg</code>
- (should be re-masked!) the value of `github_token` should be <code>ghs_&#8203;QWfPKcXBTYSB0aq7Q0OSGquUao9R2W0b&#8203;Im07</code>

> [!IMPORTANT]
> In the list above the value of GitHub-secret `MY_READONLY_TOKEN`is displayed _un-masked_ (no asterisks as usually),
> which is not allowed to be displayed in a normal scenario, because it violates
> the principles why those secrets are introduced at all (nobody must be able to see and to know them).
> But if we know definitely that this _secret_ is _not a secret_ (like here where we intentionally give the users the safe and **readonly** access), 
> it would be OK to _un-mask_ those values and make them readable.
 
> [!CAUTION] 
> **AS FOR `github_token` AND `MY_ADMIN_TOKEN` - THEY MUST BE RE-MASKED AND UN-OBFUSCATED** 

After that you can use the latest internal release of **`core-utils`**
in any maven/gradle project (from command-line and with IDE).
As for using via JBang - there could be some troubles resolving
that additional remote maven-repository and one of single-time workaround
is to download the corresponding manually with command `mvn dependency:get`:
```bash
...> mvn dependency:get -Dartifact=io.github.krm-demo:core-utils:21.09.001
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< org.apache.maven:standalone-pom >-------------------
[INFO] Building Maven Stub Project (No POM) 1
[INFO] --------------------------------[ pom ]---------------------------------
[INFO] 
[INFO] --- dependency:3.7.0:get (default-cli) @ standalone-pom ---
[INFO] Resolving io.github.krm-demo:core-utils:jar:21.02.002 with transitive dependencies
Downloading from central: https://repo.maven.apache.org/maven2/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.pom
Downloading from github: https://maven.pkg.github.com/krm-demo/*/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.pom
Downloaded from github: https://maven.pkg.github.com/krm-demo/*/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.pom (23 kB at 24 kB/s)
Downloading from central: https://repo.maven.apache.org/maven2/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.jar
Downloading from github: https://maven.pkg.github.com/krm-demo/*/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.jar
Downloaded from github: https://maven.pkg.github.com/krm-demo/*/io/github/krm-demo/core-utils/21.02.002/core-utils-21.02.002.jar (90 kB at 134 kB/s)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.549 s
[INFO] Finished at: 2025-09-29T14:26:04-04:00
[INFO] ------------------------------------------------------------------------
```
Once the artifact is downloaded (installed at local maven-repository)
JBang will recognize it and will work with it as with any other artifact:


### [Maven](https://maven.apache.org/)
Dependencies for projects that are using [Apache Maven](https://maven.apache.org/) as a build-tool 
could be declared in `pom.xml` file in following way:
- for main-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.09.001</artifactId>
        </dependency>
    ```
- for test-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.09.001</artifactId>
            <scope>test</scope>
        </dependency>
    ```
### [Gradle](https://gradle.org/)
Projects that use [**Gradle** Build Tool](https://gradle.org/) should declare the dependencies 
in either `build.gradle` or `build.gradle.kts` like following: 
```Gradle
    // for main-source implementation dependencies:
    implementation("io.github.krm-demo:core-utils:21.09.001")
    . . . . . . . . . . . . . .
    // for test-source implementation dependencies:
    testImplementation("io.github.krm-demo:core-utils:21.09.001")
```

### [JBang](https://www.jbang.dev/)
If you don't have [JBang](https://www.jbang.dev/) installed - it's very easy to do
either following by [instructions at their site](https://www.jbang.dev/download/)
or using [sdkman](https://sdkman.io/sdks/jbang/). The easiest way to verify that
both [JBang](https://www.jbang.dev/) and **`core-utils`**-library
are properly installed is to execute following command:

```bash
...> jbang io.github.krm-demo:core-utils:21.09.001
This is a Main-class of 'core-utils' library (just a test message here)
```
You must see quite the same output as above. The versions and other detailed information could also be verified in such way.

Then you can create your own jbang-scripts using following examples:
- JBang-script to see environment variables
- JBang-script to see Java system-properties
- JBang-script to see the detailed information of CLASSPATH at runtime
- ... some other very useful and helpful scripts ...
-

---
> [!TIP]
> TODO: examples to be provided... (maybe specific for _INTERNAL_-releases)
---



---

> [!NOTE]
> <small>This GitHub-Markdown page is generated with use of **`th-tool`** 
> during GitHub Workflow [`release-internal`](https://github.com/krm-demo/core-utils/actions/runs/18211981103).
> In order to make changes in this page - edit the `th-tool`-template 
> [ROOT-Readme.md.th](https://github.com/krm-demo/core-utils/blob/main/.github/th-templates/ROOT-Readme.md.th)</small>

---

the content of `GitHelper` instance (`th-tool`-expression `${git}`) is:
```json
{
  "gitRepoDir": "/home/runner/work/core-utils/core-utils/.",
  "remoteUrls": {
    "origin": "https://github.com/krm-demo/core-utils"
  },
  "gitStatus": {
    "clean": "false",
    "added": [],
    "changed": [
      "pom.xml"
    ],
    "removed": [],
    "modified": [
      ".github/th-vars/var-github.json",
      ".github/th-vars/var-githubInputs.json",
      ".github/th-vars/var-secrets.json"
    ],
    "conflicting": [],
    "untracked": [
      ".github/th-vars/var-githubJob.json",
      ".github/th-vars/var-githubSteps.json",
      "pom.xml.versionsBackup"
    ],
    "untrackedFolders": [],
    "conflictingStageState": {},
    "ignoredNotInIndex": [
      "target"
    ],
    "uncommittedChanges": [
      ".github/th-vars/var-github.json",
      ".github/th-vars/var-githubInputs.json",
      "pom.xml",
      ".github/th-vars/var-secrets.json"
    ],
    "missing": []
  }
}
```

the content of `GithubInputsHelper` (`th-tool`-expression `${gih}`) instance is:
```json
{
  "githubInputs": {},
  "releasing": "false",
  "releasingInternal": "false",
  "releasingPublic": "false",
  "renderingMainPhase": "false",
  "renderingNextPhase": "false"
}
```

the content of `MavenHelper` (`th-tool`-expression `${mh}`) instance is:
```json
{
  "calculatedProjectVersion": "21.09.001-SNAPSHOT",
  "currentProjectVersion": "21.09.001",
  "incrementalAsInt": "1",
  "incrementalVersion": "1",
  "internalNextVersion": "21.09.002-SNAPSHOT",
  "internalReleaseVersion": "21.09.001",
  "majorVersion": "21",
  "majorVersionAsInt": "21",
  "minorVersion": "9",
  "minorVersionAsInt": "9",
  "projectArtifact": "core-utils",
  "projectBadgeName": "core--utils:21.09.001",
  "projectCatalogName": "core-utils-21.09.001",
  "projectName": "core-utils:21.09.001",
  "publicNextVersion": "21.10.001-SNAPSHOT",
  "publicReleaseVersion": "21.09",
  "resourcePath": "/META-INF/maven/maven-project.properties",
  "usageFragmentPath": ".github/th-templates/Usage-INTERNAL.md.th",
  "usageFragmentSuffix": "INTERNAL",
  "versionHasIncrementalPart": "true",
  "versionHasQualifierPart": "false",
  "versionQualifier": "SNAPSHOT"
}
```

