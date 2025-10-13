[![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg?event=push)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)
![GitHub last commit](https://img.shields.io/github/last-commit/krm-demo/core-utils)
![GitHub recent commits](https://img.shields.io/github/commits-since/krm-demo/core-utils/21.07)

[![Release-Catalog](https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils/) [![Latest-Public](https://img.shields.io/badge/core--utils-21.09-blue?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils/core-utils-21.09) [![Latest-Internal](https://img.shields.io/badge/core--utils-21.10.004-blue?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils/core-utils-21.10.004) [![Snapshot-Version](https://img.shields.io/badge/core--utils-21.10.005--SNAPSHOT-blue?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils/core-utils-21.10.005-SNAPSHOT)  
[![Project-Site](https://img.shields.io/badge/GH--Pages-core--utils:21.10.005--SNAPSHOT-blue)](https://krm-demo.github.io/core-utils/core-utils-21.10.005-SNAPSHOT)

# <u>core-utils</u>

This Java-library contains utility-classes to simplify working with core-java API (streams, collections, io, ...).
It was initially created as a core and reusable part of **`th-tool`** (which is not published yet).
Some features and approaches could be very useful and helpful independently as a separate Java-library
that could be easily integrated into any Java-project as a maven/gradle artifact or as a dependency to jbang-script.

Full documentation (including JavDoc) and examples fort this particular version are available [here](https://krm-demo.github.io/core-utils/core-utils-21.10.005-SNAPSHOT).
Other versions of ths project are listed in the [release catalog](https://krm-demo.github.io/core-utils/),
but the latest relevant versions (with relation to the current one) are:

> [!TIP]
> TODO: to be inserted the header section of [Release-Catalog](https://krm-demo.github.io/core-utils/)

---

This particular version of the project `21.10.005-SNAPSHOT` is a _SNAPSHOT_-version, which consists of:
- major version `21` (that corresponds to minimum available version of JDK);
- minor version `10` (the ordinal number of _PUBLIC_-release);
- incremental version  `5` (the ordinal number of _INTERNAL_-release after the latest _PUBLIC_-release);
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
As for content of GitHub Markdown files (like one you are reading now) - they are generated and updated
during either INTERNAL-release or PUBLIC-release from corresponding `th-tool`-templates (quite all of them
are located in [this folder](.github/th-templates)).

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
            <version>21.10.005-SNAPSHOT</artifactId>
        </dependency>
    ```
- for test-source dependencies:
    ```XML
        <dependency>
            <groupId>io.github.krm-demo</groupId>
            <artifactId>core-utils</artifactId>
            <version>21.10.005-SNAPSHOT</artifactId>
            <scope>test</scope>
        </dependency>
    ```
### [Gradle](https://gradle.org/)
Projects that use [**Gradle** Build Tool](https://gradle.org/) should declare the dependencies 
in either `build.gradle` or `build.gradle.kts` like following: 
```Gradle
    // for main-source implementation dependencies:
    implementation("io.github.krm-demo:core-utils:21.10.005-SNAPSHOT")
    . . . . . . . . . . . . . .
    // for test-source implementation dependencies:
    testImplementation("io.github.krm-demo:core-utils:21.10.005-SNAPSHOT")
```

### [JBang](https://www.jbang.dev/)

If you don't have [JBang](https://www.jbang.dev/) installed - it's very easy to do
either following by [instructions at their site](https://www.jbang.dev/download/) 
or using [sdkman](https://sdkman.io/sdks/jbang/). The easiest way to verify that
both [JBang](https://www.jbang.dev/) and **`core-utils`**-library 
are properly installed is to execute following command:

```bash
...> jbang io.github.krm-demo:core-utils:21.10.005-SNAPSHOT
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
> during GitHub Workflow [`release-internal`](https://github.com/krm-demo/core-utils/actions/runs/18472391952).
> In order to make changes in this page - edit the `th-tool`-template 
> [ROOT-Readme.md.th](https://github.com/krm-demo/core-utils/blob/main/.github/th-templates/ROOT-Readme.md.th)</small>

---

the content of `GitHelper` instance (`th-tool`-expression `${git}`) is:
```json
{
  "releaseCatalog": {
    "current-minor-groups": [
      {
        "minor-group-info": "<< INTERNAL 21.10.004 >> 4 working commits",
        "commits-one-line": [
          "317a790 | 2025-10-13 Mon 16:36:08 |  << internal release >> 21.10.004",
          "c6c25a1 | 2025-10-13 Mon 16:21:04 |  display the bage with Snapshot-JavaDoc #3",
          "7850732 | 2025-10-13 Mon 16:13:10 |  << internal release >> 21.10.004",
          "e921ad2 | 2025-10-13 Mon 16:10:43 |  display the bage with Snapshot-JavaDoc #2",
          "b76bc72 | 2025-10-13 Mon 14:48:43 |  << new snapshot version >> 21.10.004-SNAPSHOT"
        ]
      },
      {
        "minor-group-info": "<< INTERNAL 21.10.003 >> 2 working commits",
        "commits-one-line": [
          "aac00af | 2025-10-13 Mon 14:46:27 |  << internal release >> 21.10.003",
          "f388bc1 | 2025-10-13 Mon 14:40:21 |  display the bage with Snapshot-JavaDoc #1",
          "9179366 | 2025-10-13 Mon 07:39:07 |  << new snapshot version >> 21.10.003-SNAPSHOT"
        ]
      },
      {
        "minor-group-info": "<< INTERNAL 21.10.002 >> 2 working commits",
        "commits-one-line": [
          "b2b6e45 | 2025-10-13 Mon 07:36:52 |  << internal release >> 21.10.002",
          "8a27da3 | 2025-10-13 Mon 07:27:37 |  display the bage with Latest-Internal release",
          "4301f22 | 2025-10-13 Mon 06:47:41 |  << new snapshot version >> 21.10.002-SNAPSHOT"
        ]
      },
      {
        "minor-group-info": "<< INTERNAL 21.10.001 >> 3 working commits",
        "commits-one-line": [
          "483f629 | 2025-10-13 Mon 06:45:44 |  << internal release >> 21.10.001",
          "87851a5 | 2025-10-13 Mon 06:43:14 |  introduce rendering of 'Shields IO' static badges by GithubBadgeHelper (use fetch-depth:0) #4",
          "c054d0a | 2025-10-13 Mon 06:04:48 |  introduce rendering of 'Shields IO' static badges by GithubBadgeHelper #3",
          "cf1c12f | 2025-10-13 Mon 02:52:08 |  << new snapshot version >> 21.10.001-SNAPSHOT"
        ]
      }
    ],
    "major-groups": [
      {
        "major-group-info": "21.09 (finalized major group with 2 finalized minor groups)",
        "final-minor-group": {
          "minor-group-info": "<< PUBLIC 21.09 >> 2 working commits",
          "commits-one-line": [
            "9f148cc | 2025-10-13 Mon 02:50:17 |  << public release >> 21.09",
            "32a1c70 | 2025-10-13 Mon 02:46:08 |  introduce rendering of 'Shields IO' static badges by GithubBadgeHelper #2",
            "dd279c9 | 2025-10-13 Mon 02:19:45 |  << new snapshot version >> 21.09.003-SNAPSHOT"
          ]
        },
        "minor-groups": [
          {
            "minor-group-info": "<< INTERNAL 21.09.002 >> 40 working commits",
            "commits-one-line": [
              "a34ed9c | 2025-10-13 Mon 02:17:41 |  << internal release >> 21.09.002",
              "1d4e7ac | 2025-10-13 Mon 02:12:54 |  introduce rendering of 'Shields IO' static badges by GithubBadgeHelper #1",
              "430ea71 | 2025-10-13 Mon 00:36:45 |  put the link to the root of Git-Hub project into the right upper cordner of 'Release Catalog' page",
              "fd13c83 | 2025-10-13 Mon 00:12:29 |  playing with css-sandbox--release-catalog #5",
              "426c4db | 2025-10-12 Sun 23:52:18 |  playing with css-sandbox--release-catalog #4",
              "816c702 | 2025-10-12 Sun 23:47:49 |  playing with css-sandbox--release-catalog #3",
              "9664501 | 2025-10-12 Sun 23:17:45 |  playing with css-sandbox--release-catalog #2",
              "c85cbc2 | 2025-10-12 Sun 20:14:42 |  playing with css-sandbox--release-catalog #1",
              "eb885a2 | 2025-10-11 Sat 06:35:37 |  reanimate 'Release Catalog' #2",
              "266c50b | 2025-10-11 Sat 06:21:36 |  reanimate 'Release Catalog' #1",
              "510a2eb | 2025-10-11 Sat 00:55:04 |  publish the test-site during 'on-main-push' workflow (put the link inro 'overview') #6",
              "98cfc9a | 2025-10-11 Sat 00:45:25 |  publish the test-site during 'on-main-push' workflow (put the link inro 'overview') #5",
              "506564b | 2025-10-11 Sat 00:24:00 |  publish the test-site during 'on-main-push' workflow (put the link inro 'overview') #4",
              "7c7277b | 2025-10-11 Sat 00:14:18 |  publish the test-site during 'on-main-push' workflow (put the link inro 'overview') #3",
              "09e46e0 | 2025-10-11 Sat 00:03:30 |  publish the test-site during 'on-main-push' workflow #2",
              "4f4e416 | 2025-10-10 Fri 23:36:49 |  publish the test-site during 'on-main-push' workflow #1",
              "0218c49 | 2025-10-10 Fri 22:40:56 |  play with test-site #2",
              "b74c01a | 2025-10-10 Fri 19:01:01 |  introduce 'CoreFileUtils.removeSilent' and play with SVG-images and test-site",
              "9cb8842 | 2025-10-09 Thu 16:27:35 |  playing with test-site and 'empty' Java-Doc #1",
              "5d7d8c8 | 2025-10-09 Thu 13:11:34 |  working on ThymeleafToolProcDir #2 (playing with test-site)",
              "ad96148 | 2025-10-09 Thu 05:29:00 |  working on ThymeleafToolProcDir #3",
              "7614dbb | 2025-10-09 Thu 03:22:32 |  introduce CoreFileUtils",
              "7651125 | 2025-10-09 Thu 01:29:05 |  working on ThymeleafToolProcDir #2",
              "23770af | 2025-10-08 Wed 02:23:01 |  Start working on ThymeleafToolProcDir",
              "40ea617 | 2025-10-07 Tue 17:38:41 |  working on 'GH-PAGES--Release-Catalog.html.th' (display git-commits) #4",
              "5b4798d | 2025-10-07 Tue 01:17:46 |  working on 'GH-PAGES--Release-Catalog.html.th' (gh-pages links) #3",
              "5f6ec4f | 2025-10-07 Tue 00:37:09 |  working on 'GH-PAGES--Release-Catalog.html.th' #2",
              "d7b7670 | 2025-10-06 Mon 20:36:31 |  working on 'GH-PAGES--Release-Catalog.html.th' #1",
              "43da185 | 2025-10-06 Mon 03:27:01 |  Format commit-time (about 30 minutes before Sunday's midnight)",
              "09df4f5 | 2025-10-06 Mon 01:26:28 |  initial version of Release-Catalog back-end - the second line of commit - and the third and the last line of commit",
              "f304164 | 2025-10-05 Sun 00:08:18 |  continue with CommitTagInfo #3",
              "c1d6fe0 | 2025-10-04 Sat 23:54:28 |  continue with CommitTagInfo #2",
              "6b098d7 | 2025-10-04 Sat 22:17:16 |  introduce CommitTagInfo #1",
              "4613011 | 2025-10-04 Sat 19:16:20 |  get rid of 'VersionTag.qualifier'",
              "4a7c513 | 2025-10-04 Sat 16:23:19 |  Introducing 'gitHelper.versionTags' and going to delete snapshot versions",
              "8b0ddea | 2025-10-04 Sat 10:10:28 |  introduce VersionTag #1",
              "d7382c7 | 2025-10-04 Sat 01:12:50 |  working with templates '.github/th-templates/GH-PAGES--xxx' #2",
              "0a8d694 | 2025-10-04 Sat 01:01:56 |  working with templates '.github/th-templates/GH-PAGES--xxx' #1",
              "151c7a8 | 2025-10-04 Sat 00:37:19 |  introduce '.github/th-templates/GH-PAGES--xxx'",
              "edf8ddc | 2025-10-03 Fri 18:04:37 |  introduce 'thtool.helpers' package and refactor accordingly",
              "78d731d | 2025-10-03 Fri 03:28:41 |  << new snapshot version >> 21.09.002-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.09.001 >> 4 working commits",
            "commits-one-line": [
              "463ab65 | 2025-10-03 Fri 03:26:44 |  << internal release >> 21.09.001",
              "7467f58 | 2025-10-03 Fri 03:22:53 |  continue with  GitHelper.java #2",
              "17481a7 | 2025-10-03 Fri 02:46:32 |  continue with  GitHelper.java #1",
              "119c139 | 2025-10-03 Fri 02:29:36 |  introduce GitHelper.java",
              "fd400d0 | 2025-10-02 Thu 21:16:06 |  << new snapshot version >> 21.09.001-SNAPSHOT"
            ]
          }
        ]
      },
      {
        "major-group-info": "21.08 (finalized major group with 1 finalized minor groups)",
        "final-minor-group": {
          "minor-group-info": "<< PUBLIC 21.08 >> 1 working commits",
          "commits-one-line": [
            "6932837 | 2025-10-02 Thu 21:14:23 |  << public release >> 21.08",
            "1c806a4 | 2025-10-02 Thu 21:02:16 |  << new snapshot version >> 21.08.002-SNAPSHOT"
          ]
        },
        "minor-groups": [
          {
            "minor-group-info": "<< INTERNAL 21.08.001 >> 6 working commits",
            "commits-one-line": [
              "d1f9e86 | 2025-10-02 Thu 21:00:21 |  << internal release >> 21.08.001",
              "9ec2049 | 2025-10-02 Thu 20:54:57 |  refactor helpers of th-tool #5 (fix badges verification)",
              "5e37ba3 | 2025-10-02 Thu 20:43:58 |  refactor helpers of th-tool #4 (fix badges verification)",
              "279a728 | 2025-10-02 Thu 20:34:08 |  refactor helpers of th-tool #3 (fix badges verification)",
              "f608548 | 2025-10-02 Thu 20:20:30 |  refactor helpers of th-tool #2",
              "bba4889 | 2025-10-02 Thu 19:19:49 |  refactor helpers of th-tool #1",
              "99eecec | 2025-10-02 Thu 05:04:48 |  << new snapshot version >> 21.08.001-SNAPSHOT"
            ]
          }
        ]
      },
      {
        "major-group-info": "21.07 (finalized major group with 1 finalized minor groups)",
        "final-minor-group": {
          "minor-group-info": "<< PUBLIC 21.07 >> 1 working commits",
          "commits-one-line": [
            "fde4a94 | 2025-10-02 Thu 05:03:16 |  << public release >> 21.07",
            "bb2d584 | 2025-10-02 Thu 04:58:17 |  << new snapshot version >> 21.07.002-SNAPSHOT"
          ]
        },
        "minor-groups": [
          {
            "minor-group-info": "<< INTERNAL 21.07.001 >> 2 working commits",
            "commits-one-line": [
              "9b31cb7 | 2025-10-02 Thu 04:56:16 |  << internal release >> 21.07.001",
              "cb2d663 | 2025-10-02 Thu 04:51:24 |  Insert 'Usage' into 'Readme' by replacing the Thymeleaf-Fragment #4",
              "977795f | 2025-10-02 Thu 04:36:32 |  << new snapshot version >> 21.07.001-SNAPSHOT"
            ]
          }
        ]
      },
      {
        "major-group-info": "21.06 (finalized major group with 3 finalized minor groups)",
        "final-minor-group": {
          "minor-group-info": "<< PUBLIC 21.06 >> 1 working commits",
          "commits-one-line": [
            "2f5d0b2 | 2025-10-02 Thu 04:34:54 |  << public release >> 21.06",
            "32ef1c0 | 2025-10-02 Thu 04:30:47 |  << new snapshot version >> 21.06.004-SNAPSHOT"
          ]
        },
        "minor-groups": [
          {
            "minor-group-info": "<< INTERNAL 21.06.003 >> 2 working commits",
            "commits-one-line": [
              "d6c8045 | 2025-10-02 Thu 04:28:54 |  << internal release >> 21.06.003",
              "05d4e6a | 2025-10-02 Thu 04:19:45 |  Insert 'Usage' into 'Readme' by replacing the Thymeleaf-Fragment #3",
              "b995a69 | 2025-10-02 Thu 03:12:41 |  << new snapshot version >> 21.06.003-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.06.002 >> 2 working commits",
            "commits-one-line": [
              "02bbc55 | 2025-10-02 Thu 03:10:42 |  << internal release >> 21.06.002",
              "eb4c5c3 | 2025-10-02 Thu 03:05:18 |  Insert 'Usage' into 'Readme' by replacing the Thymeleaf-Fragment #2",
              "8b96f5d | 2025-10-02 Thu 02:11:43 |  << new snapshot version >> 21.06.002-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.06.001 >> 3 working commits",
            "commits-one-line": [
              "3c98142 | 2025-10-02 Thu 02:09:40 |  << internal release >> 21.06.001",
              "25a7db2 | 2025-10-02 Thu 02:06:11 |  Insert 'Usage' into 'Readme' by replacing the Thymeleaf-Fragment #1",
              "ba72b05 | 2025-10-02 Thu 01:53:32 |  Use FileTemplateResolver in ThymeleafToolProc #1",
              "0c815a0 | 2025-10-02 Thu 00:23:45 |  << new snapshot version >> 21.06.001-SNAPSHOT"
            ]
          }
        ]
      },
      {
        "major-group-info": "21.05 (finalized major group with 15 finalized minor groups)",
        "final-minor-group": {
          "minor-group-info": "<< PUBLIC 21.05 >> 1 working commits",
          "commits-one-line": [
            "51df70a | 2025-10-02 Thu 00:21:51 |  << public release >> 21.05",
            "42a0f48 | 2025-10-02 Thu 00:11:22 |  << new snapshot version >> 21.05.006-SNAPSHOT"
          ]
        },
        "minor-groups": [
          {
            "minor-group-info": "<< INTERNAL 21.05.005 >> 3 working commits",
            "commits-one-line": [
              "5121aea | 2025-10-02 Thu 00:09:21 |  << internal release >> 21.05.005",
              "d5319b6 | 2025-10-01 Wed 23:44:11 |  attempt NOT to use 'git push --force ...' during public release #1",
              "488f762 | 2025-10-01 Wed 23:34:22 |  improve ThymeleafFragmentTest.java #1",
              "fd53cd9 | 2025-10-01 Wed 22:18:17 |  << new snapshot version >> 21.05.005-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.05.004 >> 2 working commits",
            "commits-one-line": [
              "6905545 | 2025-10-01 Wed 22:16:14 |  << internal release >> 21.05.004",
              "7969501 | 2025-10-01 Wed 22:10:54 |  attempt to fix the internal release by making 'git pull origin' before 'mvn versions:set ...' #4",
              "bd05dc7 | 2025-10-01 Wed 21:55:00 |  attempt to fix the internal release by making 'git pull origin' before 'mvn versions:set ...' #3"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.05.003 >> 1 working commits",
            "commits-one-line": [
              "e0ac4f7 | 2025-10-01 Wed 21:42:43 |  << internal release >> 21.05.003",
              "94368a3 | 2025-10-01 Wed 21:04:08 |  attempt to fix the internal release by making 'git pull origin' before 'mvn versions:set ...' #2"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.05.002 >> 1 working commits",
            "commits-one-line": [
              "64d23c7 | 2025-10-01 Wed 20:58:45 |  << internal release >> 21.05.002",
              "caba27d | 2025-10-01 Wed 20:54:17 |  attempt to fix the internal release by making 'git pull origin' before 'mvn versions:set ...' #1"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.05.001 >> 82 working commits",
            "commits-one-line": [
              "ed4a7c0 | 2025-10-01 Wed 20:14:42 |  << internal release >> 21.05.001",
              "c29448f | 2025-10-01 Wed 20:09:58 |  attempt not to use 'git push --force ...' during internal release",
              "7043969 | 2025-10-01 Wed 20:00:31 |  << new snapshot version >> 21.05.001-SNAPSHOT",
              "7dbdcaa | 2025-10-01 Wed 19:22:14 |  << new snapshot version >> 21.04.010-SNAPSHOT",
              "c3a4ce5 | 2025-10-01 Wed 18:24:38 |  minor corrections of README and root OVERVIEW #3",
              "80fe8f7 | 2025-10-01 Wed 17:56:33 |  << new snapshot version >> 21.04.009-SNAPSHOT",
              "9bec0f5 | 2025-10-01 Wed 17:48:42 |  minor corrections of README and root OVERVIEW #2",
              "3e5e2d9 | 2025-10-01 Wed 16:20:33 |  << new snapshot version >> 21.04.008-SNAPSHOT",
              "4135ad5 | 2025-10-01 Wed 16:07:18 |  minor corrections of README and root OVERVIEW #1",
              "4650531 | 2025-10-01 Wed 14:51:16 |  introduce block/inline fragments tests #1",
              "c2e152a | 2025-10-01 Wed 05:42:05 |  << new snapshot version >> 21.04.007-SNAPSHOT",
              "aa88d6a | 2025-10-01 Wed 05:37:25 |  put reference to gh-action at the footer of 'Readme.md' #6",
              "4363906 | 2025-10-01 Wed 05:33:20 |  some minor improvements in escape/unescape tests",
              "95d80f8 | 2025-10-01 Wed 05:05:24 |  put reference to gh-action at the footer of 'Readme.md' #5",
              "4947869 | 2025-10-01 Wed 04:53:00 |  << new snapshot version >> 21.04.006-SNAPSHOT",
              "9b0944c | 2025-10-01 Wed 04:47:50 |  put reference to gh-action at the footer of 'Readme.md' #4",
              "95f4094 | 2025-10-01 Wed 04:30:06 |  << new snapshot version >> 21.04.005-SNAPSHOT",
              "a69c1a3 | 2025-10-01 Wed 04:24:00 |  put reference to gh-action at the footer of 'Readme.md' #3",
              "893d380 | 2025-10-01 Wed 04:09:34 |  << new snapshot version >> 21.04.004-SNAPSHOT",
              "be523bc | 2025-10-01 Wed 04:05:30 |  put reference to gh-action at the footer of 'Readme.md' #2",
              "916c4d0 | 2025-10-01 Wed 03:52:03 |  << new snapshot version >> 21.04.003-SNAPSHOT",
              "16ee76b | 2025-10-01 Wed 03:47:05 |  put reference to gh-action at the footer of 'Readme.md' #1",
              "dfd063f | 2025-10-01 Wed 02:40:52 |  improve thymeleaf-tests #2",
              "d273017 | 2025-10-01 Wed 01:05:58 |  improve thymeleaf-tests #1",
              "7c375ff | 2025-09-30 Tue 18:57:08 |  << new snapshot version >> 21.04.002-SNAPSHOT",
              "dca9819 | 2025-09-30 Tue 18:44:27 |  playing with conditional fragments for 'README.md' #1",
              "4d91029 | 2025-09-30 Tue 15:29:00 |  << new snapshot version >> 21.04.001-SNAPSHOT",
              "339ba47 | 2025-09-30 Tue 15:16:59 |  << new snapshot version >> 21.03.002-SNAPSHOT",
              "34bc8f8 | 2025-09-30 Tue 15:04:26 |  rendering 'Readme.md' #1",
              "de70e38 | 2025-09-30 Tue 07:45:58 |  << new snapshot version >> 21.03.001-SNAPSHOT",
              "e6a3188 | 2025-09-30 Tue 07:31:43 |  << new snapshot version >> 21.02.005-SNAPSHOT",
              "cc900bc | 2025-09-30 Tue 07:14:56 |  Process 'Usage-XXX.md.th' templates in 'on-main-push' workflow #2",
              "558f112 | 2025-09-30 Tue 07:04:42 |  Process 'Usage-XXX.md.th' templates in 'on-main-push' workflow #1",
              "c49e85c | 2025-09-30 Tue 06:41:37 |  << new snapshot version >> 21.02.004-SNAPSHOT",
              "8a48bc2 | 2025-09-30 Tue 04:57:46 |  improve the help/usage of 'th-tool' #3",
              "850ffd9 | 2025-09-30 Tue 04:52:04 |  improve the help/usage of 'th-tool' #2",
              "c6d50fd | 2025-09-30 Tue 04:51:44 |  improve the help/usage of 'th-tool' #1",
              "145f6af | 2025-09-30 Tue 03:42:48 |  Introduce (split the original) 3 versions of 'Usage-XXX.md.th' #2",
              "52f0ae2 | 2025-09-30 Tue 02:58:38 |  Introduce (split the original) 3 versions of 'Usage-XXX.md.th'",
              "46be759 | 2025-09-29 Mon 22:04:32 |  attempt to use 'secrets' var in 'Maven-Settings.md.th' #2",
              "4377ac6 | 2025-09-29 Mon 21:12:18 |  attempt to use 'secrets' var in 'Maven-Settings.md.th' #1",
              "510722f | 2025-09-29 Mon 19:09:15 |  process  during  #2",
              "f6f075b | 2025-09-29 Mon 18:52:59 |  process  during  #1",
              "4a3f025 | 2025-09-29 Mon 18:02:22 |  inherit secrets when invoking 'on-main-push'",
              "9c2e926 | 2025-09-29 Mon 17:41:19 |  Introduce ZeroSpaceHelper",
              "1de464e | 2025-09-29 Mon 03:37:58 |  << new snapshot version >> 21.02.003-SNAPSHOT",
              "f1ec2bc | 2025-09-29 Mon 03:32:43 |  Perform the deployment into Git-Hub artifactory #1",
              "65cb4fe | 2025-09-29 Mon 02:34:08 |  << new snapshot version >> 21.02.002-SNAPSHOT",
              "da6f23d | 2025-09-29 Mon 02:28:32 |  minor test changes in 'src/main/javadoc/overview.html'",
              "0fd253c | 2025-09-29 Mon 02:02:33 |  << new snapshot version >> 21.02.001-SNAPSHOT",
              "bbfcde9 | 2025-09-29 Mon 01:54:02 |  << new snapshot version >> 21.01.004-SNAPSHOT",
              "cce1fb7 | 2025-09-29 Mon 01:40:18 |  Working on 'release-XXX.yml' #7",
              "8b89855 | 2025-09-29 Mon 01:27:11 |  << new snapshot version >> 21.01.003-SNAPSHOT",
              "5e379f2 | 2025-09-29 Mon 01:23:47 |  Working on 'release-XXX.yml' #6",
              "02dcb1c | 2025-09-29 Mon 01:11:28 |  Working on 'release-XXX.yml' #5",
              "522a674 | 2025-09-29 Mon 00:39:09 |  Working on 'release-XXX.yml' #4",
              "d6bf5f8 | 2025-09-29 Mon 00:36:41 |  Working on 'release-XXX.yml' #3",
              "cc491d3 | 2025-09-29 Mon 00:34:58 |  Working on 'release-XXX.yml' #2",
              "967455a | 2025-09-29 Mon 00:16:37 |  Working on 'release-XXX.yml' #1",
              "690abda | 2025-09-28 Sun 23:24:07 |  << new snapshot version >> 21.01.002-SNAPSHOT",
              "59f4fc4 | 2025-09-28 Sun 23:18:13 |  << new snapshot version >> 21.01.001-SNAPSHOT",
              "5e2d06f | 2025-09-28 Sun 23:11:09 |  Start working on 'release-public.yml' #1",
              "4a7725e | 2025-09-28 Sun 22:35:25 |  << new snapshot version >> 21.00.017-SNAPSHOT",
              "5fc952a | 2025-09-28 Sun 22:32:17 |  continue with release workflows #12",
              "83f55ad | 2025-09-28 Sun 22:16:55 |  << new snapshot version >> 21.00.016-SNAPSHOT",
              "d35517f | 2025-09-28 Sun 22:12:10 |  continue with release workflows #11",
              "0181cde | 2025-09-28 Sun 22:00:09 |  continue with release workflows #10",
              "bc0385a | 2025-09-28 Sun 21:51:44 |  continue with release workflows #9",
              "bdd4b87 | 2025-09-28 Sun 21:49:25 |  continue with release workflows #8",
              "e5ab953 | 2025-09-28 Sun 21:05:13 |  << new snapshot version >> 21.00.015-SNAPSHOT",
              "65c33c9 | 2025-09-28 Sun 20:59:40 |  continue with release workflows #7",
              "59240f0 | 2025-09-28 Sun 20:29:36 |  << new snapshot version >> 21.00.014-SNAPSHOT",
              "905d4c2 | 2025-09-28 Sun 20:17:42 |  continue with release workflows #6",
              "71c2e7d | 2025-09-28 Sun 19:59:04 |  << new snapshot version >> 21.00.013-SNAPSHOT",
              "d4d9042 | 2025-09-28 Sun 19:48:53 |  continue with release workflows #5",
              "efc8450 | 2025-09-28 Sun 19:46:13 |  continue with release workflows #4",
              "e182e2f | 2025-09-28 Sun 19:36:35 |  continue with release workflows #3",
              "17a730e | 2025-09-28 Sun 19:34:25 |  continue with release workflows #2",
              "ac54179 | 2025-09-28 Sun 19:30:16 |  continue with release workflows #1",
              "aa9a20d | 2025-09-28 Sun 08:44:19 |  << new snapshot version >> 21.00.012-SNAPSHOT",
              "bc628dc | 2025-09-28 Sun 08:40:38 |  Working on '.github/workflows/release-internal.yml' #13",
              "0c03e00 | 2025-09-28 Sun 08:18:04 |  << new snapshot version >> 21.00.011-SNAPSHOT",
              "8f44124 | 2025-09-28 Sun 08:08:37 |  Working on '.github/workflows/release-internal.yml' #12"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.009 >> 1 working commits",
            "commits-one-line": [
              "dee53af | 2025-09-28 Sun 08:03:33 |  << internal release >> 21.00.009",
              "847e51a | 2025-09-28 Sun 08:01:12 |  Working on '.github/workflows/release-internal.yml' #11"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.008 >> 1 working commits",
            "commits-one-line": [
              "f4b6995 | 2025-09-28 Sun 07:40:36 |  << internal release >> 21.00.008",
              "d782c2c | 2025-09-28 Sun 07:37:55 |  Working on '.github/workflows/release-internal.yml' #10"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.007 >> 3 working commits",
            "commits-one-line": [
              "0a9ade4 | 2025-09-28 Sun 07:17:09 |  << internal release >> 21.00.007",
              "0b1cf24 | 2025-09-28 Sun 07:14:22 |  Working on '.github/workflows/release-internal.yml' #9",
              "eb377b1 | 2025-09-28 Sun 07:03:32 |  Working on '.github/workflows/release-internal.yml' #8",
              "42eba76 | 2025-09-28 Sun 06:47:49 |  Working on '.github/workflows/release-internal.yml' #7"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.006 >> 2 working commits",
            "commits-one-line": [
              "8ebd971 | 2025-09-28 Sun 05:56:39 |  << internal release >> 21.00.006",
              "03717c7 | 2025-09-28 Sun 05:44:17 |  Working on '.github/workflows/release-internal.yml' #6",
              "b3954aa | 2025-09-28 Sun 05:15:55 |  << new snapshot version >> 21.00.006-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.005 >> 2 working commits",
            "commits-one-line": [
              "deb9047 | 2025-09-28 Sun 05:15:53 |  << internal release >> 21.00.005",
              "33a97e1 | 2025-09-28 Sun 05:12:43 |  Working on '.github/workflows/release-internal.yml' #5",
              "d6266ab | 2025-09-28 Sun 04:35:35 |  << new snapshot version >> 21.00.005-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.004 >> 2 working commits",
            "commits-one-line": [
              "3b04b46 | 2025-09-28 Sun 04:35:32 |  << internal release >> 21.00.004",
              "810151b | 2025-09-28 Sun 04:29:52 |  Working on '.github/workflows/release-internal.yml' #4",
              "92b86e7 | 2025-09-28 Sun 04:04:48 |  << new snapshot version >> 21.00.004-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.003 >> 2 working commits",
            "commits-one-line": [
              "ac6b12f | 2025-09-28 Sun 04:04:45 |  << internal release >> 21.00.003",
              "99af350 | 2025-09-28 Sun 03:58:58 |  Working on '.github/workflows/release-internal.yml' #3",
              "5f8460d | 2025-09-28 Sun 03:38:00 |  << new snapshot version >> 21.00.003-SNAPSHOT"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.00.002 >> 122 working commits",
            "commits-one-line": [
              "290d5eb | 2025-09-28 Sun 03:37:58 |  << internal release >> 21.00.002",
              "6a30075 | 2025-09-28 Sun 03:29:06 |  Working on '.github/workflows/release-internal.yml' #2",
              "13f6e3d | 2025-09-28 Sun 02:46:31 |  start minor version from '001'",
              "6742e29 | 2025-09-28 Sun 02:29:58 |  Start working on '.github/workflows/release-internal.yml' #1",
              "d1e95e1 | 2025-09-28 Sun 01:01:33 |  Use Sl4j everywhere in th-tool #3",
              "89a0c5c | 2025-09-28 Sun 00:16:11 |  Use Sl4j everywhere in th-tool #2",
              "139bd4b | 2025-09-27 Sat 23:13:28 |  Use Sl4j everywhere in th-tool #1",
              "97cb92a | 2025-09-27 Sat 20:03:32 |  Introduce MavenHelper #1",
              "0ac0463 | 2025-09-27 Sat 03:51:14 |  Test and debug th-tool (release versions) #9",
              "b6e5771 | 2025-09-27 Sat 03:36:30 |  Test and debug th-tool (release versions) #8",
              "05b11c3 | 2025-09-27 Sat 03:11:16 |  Test and debug th-tool (release versions) #7",
              "f437fbc | 2025-09-27 Sat 03:09:36 |  Test and debug th-tool (release versions) #5",
              "2eb81de | 2025-09-27 Sat 03:01:52 |  Test and debug th-tool (release versions) #5",
              "0d4104e | 2025-09-27 Sat 02:52:38 |  Test and debug th-tool (release versions) #4",
              "1d05416 | 2025-09-27 Sat 02:43:39 |  Test and debug th-tool (release versions) #3",
              "edd9d65 | 2025-09-27 Sat 01:29:15 |  Test and debug th-tool #2",
              "6545621 | 2025-09-27 Sat 00:54:50 |  Test and debug th-tool #1",
              "5127dfd | 2025-09-27 Sat 00:16:34 |  Introduce 2 subcommands in 'th-tool' #6",
              "5b6fc69 | 2025-09-26 Fri 23:47:50 |  Introduce 2 subcommands in 'th-tool' #5",
              "3b0dd90 | 2025-09-26 Fri 20:00:06 |  Introduce 2 subcommands in 'th-tool' #4",
              "cca85d7 | 2025-09-26 Fri 19:24:16 |  Introduce 2 subcommands in 'th-tool' #3",
              "deee1a9 | 2025-09-26 Fri 19:23:46 |  Introduce 2 subcommands in 'th-tool' #2",
              "e508a30 | 2025-09-26 Fri 19:07:52 |  Introduce 2 subcommands in 'th-tool' #1",
              "0ca5575 | 2025-09-25 Thu 04:04:04 |  correct dump to summary and delete 'misc--gh-pages.yml' GitHub-workflow",
              "0635712 | 2025-09-25 Thu 03:57:01 |  Introduce 'maven-source-plugin' and upgrade the maven deps",
              "ecf464e | 2025-09-25 Thu 02:48:18 |  Add and improve some JavaDoc #1",
              "0555757 | 2025-09-25 Thu 02:17:33 |  Publish to GH-Pages as a part of 'on-main-push' action for each commit",
              "cd0c9ad | 2025-09-25 Thu 01:49:19 |  Switch to JDK '25' and use '(Amazon Corretto)' vendor everywhere in GitHub actions",
              "a3bf220 | 2025-09-25 Thu 00:35:11 |  temporary use the result of 'javadoc:jar' MOJO #1",
              "8cd43fa | 2025-09-25 Thu 00:04:06 |  fix the detection of target core-utils-21.0.2-SNAPSHOT.jar",
              "ee41e52 | 2025-09-24 Wed 21:40:32 |  attach JavaDoc's JAR into the default build-lifecycle",
              "da39c9f | 2025-09-24 Wed 19:11:47 |  some minor corrections to JavaDoc #2",
              "eb952b4 | 2025-09-24 Wed 04:37:20 |  some minor corrections to JavaDoc #1",
              "34ea826 | 2025-09-24 Wed 03:02:54 |  attempt to publish git-log and javadoc into gh-pages #3",
              "70f117c | 2025-09-24 Wed 01:30:34 |  attempt to publish git-log and javadoc into gh-pages #2",
              "c25d74a | 2025-09-24 Wed 01:16:38 |  attempt to publish git-log and javadoc into gh-pages #1",
              "187a38c | 2025-09-23 Tue 23:45:58 |  verify append to file and git-add the whole directory as '.'",
              "e513459 | 2025-09-23 Tue 23:26:01 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #9",
              "08885a3 | 2025-09-23 Tue 23:11:15 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #8",
              "bd9b0f8 | 2025-09-23 Tue 22:53:18 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #7",
              "5ccc853 | 2025-09-23 Tue 21:52:41 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #6",
              "0be7090 | 2025-09-23 Tue 21:35:45 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #5",
              "e3269a4 | 2025-09-23 Tue 20:57:25 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #4",
              "9fbd49a | 2025-09-23 Tue 04:43:23 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #3",
              "b9c9275 | 2025-09-23 Tue 04:39:00 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages #2",
              "8eef705 | 2025-09-23 Tue 04:25:53 |  intrdouce 'misc--gh-pages.yml' action to play with gh-pages",
              "c0bec9b | 2025-09-23 Tue 02:56:29 |  add badge to GH-Pages #2",
              "2a48829 | 2025-09-23 Tue 02:55:50 |  add badge to GH-Pages",
              "2a301ce | 2025-09-23 Tue 01:05:47 |  Attempt to fix the build with all names at '@JsonPropertyOrder' annotation",
              "7c5ac17 | 2025-09-23 Tue 00:49:40 |  intrdouce 'javadoc' MOJO and attempt to fix the build with '@JsonPropertyOrder'",
              "34d84dd | 2025-09-22 Mon 22:20:49 |  refactor - introduce 'core' sub-package",
              "f3e6231 | 2025-09-22 Mon 19:45:46 |  refactor assertions of ObjectPrinterTest",
              "96cd4f2 | 2025-09-22 Mon 19:01:24 |  implementing SVG-dumpers (YAML) #5",
              "b6d6bf3 | 2025-09-22 Mon 18:44:22 |  implementing SVG-dumpers (YAML) #4",
              "5f0b925 | 2025-09-22 Mon 17:32:13 |  implementing SVG-dumpers (finish with JSON) #3",
              "c2afb7d | 2025-09-22 Mon 09:53:32 |  implementing SVG-dumpers (JSON) #2",
              "22017ea | 2025-09-22 Mon 05:22:31 |  implementing SVG-dumpers (JSON) #1",
              "5661872 | 2025-09-22 Mon 02:48:32 |  introduce OuterTagUtils #2",
              "447451a | 2025-09-22 Mon 01:35:13 |  introduce OuterTagUtils",
              "848620d | 2025-09-21 Sun 01:37:04 |  implementing YAML rendering into HTML #4",
              "69c4b32 | 2025-09-21 Sun 01:25:07 |  implementing YAML rendering into HTML #3",
              "5a54755 | 2025-09-21 Sun 00:31:32 |  implementing YAML rendering #2",
              "f7d27e8 | 2025-09-20 Sat 23:33:41 |  implementing YAML rendering #1",
              "1c8cb80 | 2025-09-20 Sat 03:38:21 |  implementing HTML rendering #1",
              "7d60df0 | 2025-09-18 Thu 01:13:16 |  introduce and use RenderSpec with Highlight instead of Highlighter",
              "0b79c6a | 2025-09-16 Tue 22:51:55 |  provide JavaDoc and refine some tests #4 (minor refactoring))",
              "898ae4c | 2025-09-15 Mon 21:17:11 |  provide JavaDoc and refine some tests #3 (testBooleans)",
              "26f2271 | 2025-09-15 Mon 17:38:12 |  provide JavaDoc and refine some tests #2",
              "5bb624d | 2025-09-15 Mon 01:53:39 |  provide JavaDoc and refine some tests #1",
              "0a2cd5b | 2025-09-12 Fri 03:52:18 |  provide the default implemntations of ObjectPrinter and introduce utility-facades #1",
              "c27cecc | 2025-09-05 Fri 02:27:09 |  implementing JarFileInfo.java #1",
              "0b853ad | 2025-09-03 Wed 02:16:34 |  continue working 'th-tool' #3",
              "5148a00 | 2025-09-03 Wed 01:14:16 |  continue working 'th-tool' #2",
              "5edb8fb | 2025-09-03 Wed 00:01:55 |  continue working 'th-tool' #1",
              "c42c1a3 | 2025-09-02 Tue 22:26:01 |  start working on the first real 'th-tool'-template",
              "e8eefc5 | 2025-09-01 Mon 22:32:30 |  support of '.properties' extension in ThymeleafVars #2",
              "4750427 | 2025-09-01 Mon 22:08:42 |  support of '.properties' extension in ThymeleafVars #1",
              "db1ef0d | 2025-09-01 Mon 03:18:31 |  Working with th-tool (correct vars-dir) #5",
              "c8f967b | 2025-09-01 Mon 03:11:55 |  Working with th-tool (add a workflow 'th-tool') #4",
              "0f11159 | 2025-09-01 Mon 03:09:46 |  Working with th-tool (add a workflow 'th-tool') #3",
              "5a004a5 | 2025-09-01 Mon 03:04:48 |  Working with th-tool (add a workflow 'th-tool') #2",
              "4ef75eb | 2025-09-01 Mon 02:59:03 |  Working with th-tool (add a workflow 'th-tool')",
              "426546d | 2025-09-01 Mon 01:56:44 |  Working with th-tool (processing vars)",
              "0cc9c96 | 2025-09-01 Mon 00:35:03 |  Introduce JacksonUtils.java",
              "b6216fa | 2025-08-30 Sat 04:03:30 |  Working with th-templates for usage (tuning the maven cache)",
              "c256030 | 2025-08-30 Sat 03:48:40 |  Working with th-templates for usage #5",
              "0d6e44c | 2025-08-30 Sat 03:46:48 |  Working with th-templates for usage #4",
              "fdc9a51 | 2025-08-30 Sat 03:45:47 |  Working with th-templates for usage #3",
              "d127c0f | 2025-08-30 Sat 00:16:00 |  Working with th-templates for usage #2",
              "70cbc7c | 2025-08-29 Fri 23:34:57 |  Working with th-templates for usage #1",
              "83efd0e | 2025-08-29 Fri 02:09:42 |  start working on ThymeleafTool #1",
              "215d49c | 2025-08-27 Wed 12:26:41 |  play with multi-line github-workflow action-step-run #4",
              "30945aa | 2025-08-27 Wed 11:51:28 |  play with multi-line github-workflow action-step-run #3",
              "0c05d10 | 2025-08-27 Wed 10:43:01 |  play with multi-line github-workflow action-step-run #2",
              "b2d9dc1 | 2025-08-27 Wed 10:31:11 |  play with multi-line github-workflow action-step-run #1",
              "44d2d9f | 2025-08-27 Wed 03:48:15 |  generate 'META-INF/maven/maven-project.properties' with parsed versions",
              "07f0fbf | 2025-08-27 Wed 01:02:19 |  improve DumpSysProps",
              "c7a1020 | 2025-08-27 Wed 00:18:43 |  improve the result of 'dump-to-summary.sh' #3",
              "ca3f231 | 2025-08-27 Wed 00:07:06 |  improve the result of 'dump-to-summary.sh' #2",
              "e10a5c4 | 2025-08-27 Wed 00:04:32 |  improve the result of 'dump-to-summary.sh' #1",
              "9ec47b7 | 2025-08-26 Tue 22:42:49 |  setup jbang in gh-workflow action",
              "d9b3ede | 2025-08-26 Tue 22:22:47 |  debug dump-to-summary.sh #2",
              "cc84ae1 | 2025-08-26 Tue 22:16:51 |  debug dump-to-summary.sh #1",
              "917cab6 | 2025-08-26 Tue 22:06:54 |  playing with dumps #9",
              "11f9af2 | 2025-08-26 Tue 22:02:00 |  playing with dumps #8",
              "40d9c73 | 2025-08-26 Tue 22:00:32 |  playing with dumps #7",
              "521a9ce | 2025-08-26 Tue 21:59:10 |  playing with dumps #6",
              "7209928 | 2025-08-26 Tue 21:35:36 |  playing with dumps #5",
              "8fa241c | 2025-08-26 Tue 21:31:32 |  playing with dumps #4",
              "7996a0c | 2025-08-26 Tue 21:28:50 |  playing with dumps #3",
              "2af60cc | 2025-08-26 Tue 21:02:27 |  playing with dumps #2",
              "930d87b | 2025-08-26 Tue 19:24:35 |  playing with dumps #1",
              "5f62f9e | 2025-08-25 Mon 01:11:41 |  Provide toSortedMap and toLinkedMap",
              "da0c1ee | 2025-08-24 Sun 20:51:17 |  Play with java and maven #1",
              "cead81f | 2025-08-24 Sun 20:38:21 |  Play with java and maven #0",
              "461e198 | 2025-08-24 Sun 02:48:11 |  make bash executable #2",
              "a6b6fe1 | 2025-08-24 Sun 02:30:29 |  make bash executable",
              "b68bfd3 | 2025-08-24 Sun 00:59:18 |  add status badge",
              "2a4d122 | 2025-08-23 Sat 21:56:22 |  playing with GH workflows #5",
              "3dd8518 | 2025-08-23 Sat 21:32:22 |  playing with GH workflows #4",
              "d6f0284 | 2025-08-23 Sat 21:23:49 |  playing with GH workflows #3",
              "c4ed4e5 | 2025-08-23 Sat 21:10:41 |  playing with GH workflows #2",
              "eaff78a | 2025-08-23 Sat 21:07:25 |  playing with GH workflows"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.0.2 >> only one technical commit",
            "commits-one-line": [
              "c975e12 | 2025-08-23 Sat 03:43:09 |  attempt to fix resources"
            ]
          },
          {
            "minor-group-info": "<< INTERNAL 21.0.1 >> 6 working commits",
            "commits-one-line": [
              "bd5fc32 | 2025-08-22 Fri 20:48:44 |  adding enforcer, versioner and other pliugins (including manifest generation)",
              "8f6f5e9 | 2025-08-22 Fri 04:43:27 |  Provide some debug-info #1",
              "4a59309 | 2025-08-22 Fri 04:21:54 |  Provide maven cache",
              "ee0dd7a | 2025-08-22 Fri 04:07:18 |  Provide <distributionManagement> section to 'pom.xml'",
              "380fe36 | 2025-08-22 Fri 03:31:04 |  Introduce some main and test code",
              "8e4641b | 2025-08-22 Fri 02:05:15 |  Create maven-publish.yml",
              "f736ec4 | 2025-08-21 Thu 21:43:02 |  Initial commit"
            ]
          }
        ]
      }
    ]
  },
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
    "modified": [
      ".github/th-vars/var-github.json",
      ".github/th-vars/var-githubInputs.json",
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
  "calculatedProjectVersion": "21.10.005-SNAPSHOT",
  "currentProjectVersion": "21.10.005-SNAPSHOT",
  "incrementalAsInt": "5",
  "incrementalVersion": "5",
  "internalNextVersion": "21.10.006-SNAPSHOT",
  "internalReleaseVersion": "21.10.005",
  "majorVersion": "21",
  "majorVersionAsInt": "21",
  "minorVersion": "10",
  "minorVersionAsInt": "10",
  "projectArtifact": "core-utils",
  "projectBadgeName": "core--utils:21.10.005--SNAPSHOT",
  "projectCatalogName": "core-utils-21.10.005-SNAPSHOT",
  "projectName": "core-utils:21.10.005-SNAPSHOT",
  "publicNextVersion": "21.11.001-SNAPSHOT",
  "publicReleaseVersion": "21.10",
  "resourcePath": "/META-INF/maven/maven-project.properties",
  "usageFragmentPath": ".github/th-templates/Usage-SNAPSHOT.md.th",
  "usageFragmentSuffix": "SNAPSHOT",
  "versionHasIncrementalPart": "true",
  "versionHasQualifierPart": "true",
  "versionQualifier": "SNAPSHOT"
}
```

