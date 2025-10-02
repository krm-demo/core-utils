[![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/krm-demo/core-utils)

[![Release-Catalog](https://img.shields.io/badge/GH--Pages-Release_Catalog-blue)](https://krm-demo.github.io/core-utils/)
[![Project-Site](https://img.shields.io/badge/GH--Pages-core--utils:21.07-blue)](https://krm-demo.github.io/core-utils/core-utils-21.07)

# <u>core-utils</u>

This Java-library contains utility-classes to simplify working with core-java API (streams, collections, io, ...).
It was initially created as a core and reusable part of **`th-tool`** (which is not published yet).
Some features and approaches could be very useful and helpful independently as a separate Java-library
that could be easily integrated into any Java-project as a maven/gradle artifact or as a dependency to jbang-script.

Full documentation (including JavDoc) and examples fort this particular version are available [here](https://krm-demo.github.io/core-utils/core-utils-21.07).
Other versions of ths project are listed in the [release catalog](https://krm-demo.github.io/core-utils/),
but the latest relevant versions (with relation to the current one) are:

> [!TIP]
> TODO: to be inserted the header section of [Release-Catalog](https://krm-demo.github.io/core-utils/)

---

> [!TIP]
> TODO: to be inserted the usage (conditionally: PUBLIC / INTERNAL / SNAPSHOT)

the content of `GithubInputsHelper` instance is:
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

the content of `MavenHelper` instance is:
```json
{
  "calculatedProjectVersion": "21.07.000-SNAPSHOT",
  "currentProjectVersion": "21.07",
  "incrementalAsInt": "0",
  "incrementalVersion": "0",
  "internalNextVersion": "21.07.001-SNAPSHOT",
  "internalReleaseVersion": "21.07.000",
  "majorVersion": "21",
  "majorVersionAsInt": "21",
  "minorVersion": "7",
  "minorVersionAsInt": "7",
  "projectArtifact": "core-utils",
  "projectBadgeName": "core--utils:21.07",
  "projectCatalogName": "core-utils-21.07",
  "projectName": "core-utils:21.07",
  "publicNextVersion": "21.08.001-SNAPSHOT",
  "publicReleaseVersion": "21.07",
  "resourcePath": "/META-INF/maven/maven-project.properties",
  "usageFragmentPath": ".github/th-templates/Usage-PUBLIC.md.th",
  "usageFragmentSuffix": "PUBLIC",
  "versionHasIncrementalPart": "false",
  "versionHasQualifierPart": "false",
  "versionQualifier": "SNAPSHOT"
}
```

This particular version of the project `21.07` is an _PUBLIC_-release version, which consists of
- major version `21` (that corresponds to minimum available version of JDK);
- minor version `7` (the ordinal number of _PUBLIC_-release);

> [!TIP]
> TODO: check whether this _PUBLIC_-release is the latest one and render the warning if it's not

## Usage of `core-utils`-library

> [!NOTE]
> The _PUBLIC_-release version has only 2 integer parts (_major_ and _minor_) parts **without** _incremental_ part and suffix `-SNAPSHOT`.
> It's built and deployed into [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages)
> artifatcory as a result of manual execution of GitHub workflow [`release-public`](https://github.com/krm-demo/core-utils/actions/workflows/release-public.yml).

This version is deployed into public [maven central repository](https://central.sonatype.com/) - so, it's available for usage
without additional downloading steps or local builds. As for Java-Doc it's either available here or will be automatically downloaded by your favorite IDE.

But in order your maven/gradle project or jbang-script to use `core-utils`-library
you must declare it as dependency in the way described below.

### [Maven](https://maven.apache.org/)
Dependencies for projects that are using [Apache Maven](https://maven.apache.org/) as a build-tool 
could be declared in `pom.xml` file in following way:
- for main-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.07</artifactId>
        </dependency>
    ```
- for test-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.07</artifactId>
            <scope>test</scope>
        </dependency>
    ```

### [Gradle](https://gradle.org/)
Projects that use [**Gradle** Build Tool](https://gradle.org/) should declare the dependencies 
in either `build.gradle` or `build.gradle.kts` like following: 
```Gradle
    // for main-source implementation dependencies:
    implementation("io.github.krm-demo:core-utils:21.07")
    . . . . . . . . . . . . . .
    // for test-source implementation dependencies:
    testImplementation("io.github.krm-demo:core-utils:21.07")
```

### [JBang](https://www.jbang.dev/)
If you don't have [JBang](https://www.jbang.dev/) installed - it's very easy to do
either following by [instructions at their site](https://www.jbang.dev/download/)
or using [sdkman](https://sdkman.io/sdks/jbang/). The easiest way to verify that
both [JBang](https://www.jbang.dev/) and **`core-utils`**-library
are properly installed is to execute following command:

```bash
...> jbang io.github.krm-demo:core-utils:21.07
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
> TODO: examples to be provided... (maybe specific for _PUBLIC_-releases)
---



---

> [!NOTE]
> <small>This GitHub-Markdown page is generated with use of **`th-tool`** 
> during GitHub Workflow [`release-public`](https://github.com/krm-demo/core-utils/actions/runs/18183871283).
> In order to make changes in this page - edit the `th-tool`-template 
> [ROOT-Readme.md.th](https://github.com/krm-demo/core-utils/blob/main/.github/th-templates/ROOT-Readme.md.th)</small>
