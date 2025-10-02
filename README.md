[![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/krm-demo/core-utils)

[![Release-Catalog](https://img.shields.io/badge/GH--Pages-Release_Catalog-blue)](https://krm-demo.github.io/core-utils/)
[![Project-Site](https://img.shields.io/badge/GH--Pages-core--utils:21.06.001--SNAPSHOT-blue)](https://krm-demo.github.io/core-utils/core-utils-21.06.001-SNAPSHOT)

# <u>core-utils</u>

This Java-library contains utility-classes to simplify working with core-java API (streams, collections, io, ...).
It was initially created as a core and reusable part of **`th-tool`** (which is not published yet).
Some features and approaches could be very useful and helpful independently as a separate Java-library
that could be easily integrated into any Java-project as a maven/gradle artifact or as a dependency to jbang-script.

Full documentation (including JavDoc) and examples fort this particular version are available [here](https://krm-demo.github.io/core-utils/core-utils-21.06.001-SNAPSHOT).
Other versions of ths project are listed in the [release catalog](https://krm-demo.github.io/core-utils/),
but the latest relevant versions (with relation to the current one) are:

> [!TIP]
> TODO: to be inserted the header section of [Release-Catalog](https://krm-demo.github.io/core-utils/)

---

> [!TIP]
> TODO: to be inserted the usage (conditionally: PUBLIC / INTERNAL / SNAPSHOT)

This particular version of the project `21.06.001-SNAPSHOT` is a _SNAPSHOT_-version, which consists of:
- major version `21` (that corresponds to minimum available version of JDK);
- minor version `6` (the ordinal number of _PUBLIC_-release);
- incremental version  `1` (the ordinal number of _INTERNAL_-release after the latest _PUBLIC_-release);
- the qualifier `SNAPSHOT` means that it's a _SNAPSHOT_-version (not released yet in any kind)

> [!IMPORTANT]
> It's highly recommended to use the latest _PUBLIC_-release version, because all others are either outdated or not stable yet

## Working with SNAPSHOT-version of `core-utils`-library

> [!NOTE]
> The SNAPSHOT-version has 3 integer parts (_major_, _minor_ and _incremental_) followed by the suffix `-SNAPSHOT`.
> It's built by GitHub workflow [`on-main-push`](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml) on every `git push` into `main` branch.

The JavaDoc is generated and project-site at [GitHub Pages](https://docs.github.com/en/pages) is updated,
but **nothing is deployed** neither to [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages)
artifactory nor to [maven central repository](https://central.sonatype.com/).

The binaries of _Snapshot_-version are available only after cloning the git-repo locally and
checking out the proper branch (the most recent snapshot is in `main`-branch).
In order to build the project it's recommended to use [sdkman](https://sdkman.io/) to install the latest version
of [java](https://sdkman.io/jdks/) and the latest version of [maven](https://sdkman.io/sdks/maven/). 
After that, staying in the root directory of a project it's enough to type:
```bash
...> mvn clean install
```
The command above installs **`core-utils`**-library 
in the local maven-repository, and it becomes available for usage described below...

### [Maven](https://maven.apache.org/)
Dependencies for projects that are using [Apache Maven](https://maven.apache.org/) as a build-tool 
could be declared in `pom.xml` file in following way:
- for main-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.06.001-SNAPSHOT</artifactId>
        </dependency>
    ```
- for test-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.06.001-SNAPSHOT</artifactId>
            <scope>test</scope>
        </dependency>
    ```
### [Gradle](https://gradle.org/)
Projects that use [**Gradle** Build Tool](https://gradle.org/) should declare the dependencies 
in either `build.gradle` or `build.gradle.kts` like following: 
```Gradle
    // for main-source implementation dependencies:
    implementation("io.github.krm-demo:core-utils:21.06.001-SNAPSHOT")
    . . . . . . . . . . . . . .
    // for test-source implementation dependencies:
    testImplementation("io.github.krm-demo:core-utils:21.06.001-SNAPSHOT")
```

### [JBang](https://www.jbang.dev/)

If you don't have [JBang](https://www.jbang.dev/) installed - it's very easy to do
either following by [instructions at their site](https://www.jbang.dev/download/) 
or using [sdkman](https://sdkman.io/sdks/jbang/). The easiest way to verify that
both [JBang](https://www.jbang.dev/) and **`core-utils`**-library 
are properly installed is to execute following command:

```bash
...> jbang io.github.krm-demo:core-utils:21.06.001-SNAPSHOT
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
> TODO: examples to be provided... (maybe specific for _SNAPSHOT_-versions)
---



---

> [!NOTE]
> <small>This GitHub-Markdown page is generated with use of **`th-tool`** 
> during GitHub Workflow [`release-internal`](https://github.com/krm-demo/core-utils/actions/runs/18181207293).
> In order to make changes in this page - edit the `th-tool`-template 
> [ROOT-Readme.md.th](https://github.com/krm-demo/core-utils/blob/main/.github/th-templates/ROOT-Readme.md.th)</small>
