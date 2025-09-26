## Installation and Usage of `core-utils`-library

_Snapshot_-versions are available only after cloning the git-repo locally and
checking out the proper branch (the most recent snapshot is in `main`-branch).
In order to build the project it's recommended to use [sdkman](https://sdkman.io/) to install the latest version
of [java](https://sdkman.io/jdks/) and the latest version of [maven](https://sdkman.io/sdks/maven/). 
After that, staying in the root directory of a project it's enough to type:
```bash
...> mvn clean install
```
The command above installs `core-utils`-library 
in the local maven-repository, and it becomes available for usage described below...

### [Maven](https://maven.apache.org/)
Dependencies for projects that are using [Apache Maven](https://maven.apache.org/) as a build-tool 
could be declared in `pom.xml` file in following way:
- for main-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.0.2-SNAPSHOT</artifactId>
        </dependency>
    ```
- for test-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.0.2-SNAPSHOT</artifactId>
            <scope>test</scope>
        </dependency>
    ```
### [Gradle](https://gradle.org/)
Projects that use [**Gradle** Build Tool](https://gradle.org/) should declare the dependencies 
in either `build.gradle` or `build.gradle.kts` like following: 
```Gradle
    // for main-source implementation dependencies:
    implementation("io.github.krm-demo:core-utils:21.0.2-SNAPSHOT")
    . . . . . . . . . . . . . .
    // for test-source implementation dependencies:
    testImplementation("io.github.krm-demo:core-utils:21.0.2-SNAPSHOT")
```

### [JBang](https://www.jbang.dev/)
TODO: to be described...

