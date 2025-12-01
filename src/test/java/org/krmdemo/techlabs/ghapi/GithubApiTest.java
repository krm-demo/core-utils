package org.krmdemo.techlabs.ghapi;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.fail;
import static org.krmdemo.techlabs.core.utils.CorePropsUtils.propValue;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.listTwiceOf;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedMap;
import static org.krmdemo.techlabs.ghapi.GithubApi.Factory.mavenPackageName;

/**
 * This unit-test checks the results of GitHub-workflow actions to be cleaned up
 * in order to save the occupied space and git rid of extra garbage in repository.
 */
public class GithubApiTest {

    static final String CURRENT_OWNER_NAME = "krm-demo";
    static final String CURRENT_REPO_NAME = "core-utils";

    static final String CURRENT_MAVEN_PROJECT_GROUP = "io.github." + CURRENT_OWNER_NAME;
    static final String CURRENT_MAVEN_PROJECT_ARTIFACT = CURRENT_REPO_NAME;

    // by convention - the name of GitHub-package's name is dot'.'-concatenated maven-group and maven-artifact:
    static final String CURRENT_GITHUB_PACKAGE_NAME = mavenPackageName(
        CURRENT_MAVEN_PROJECT_GROUP, CURRENT_MAVEN_PROJECT_ARTIFACT);

    static GithubApi githubApi = GithubApi.feignFactory()
        .ownerName(CURRENT_OWNER_NAME)
        .repoName(CURRENT_REPO_NAME)
        .mavenProjectGroup(CURRENT_MAVEN_PROJECT_GROUP)
        .mavenProjectArtifact(CURRENT_MAVEN_PROJECT_ARTIFACT)
        .githubToken(GithubHttpTest.GITHUB_AUTH_TOKEN)
        .create();

    @Test
    void testCurrentUser() {
        GithubApi.User currentUser = githubApi.getCurrentUser();
        assertThat(currentUser).isNotNull();
        System.out.println("currentUser --> " + currentUser);
        assertThat(currentUser.login()).isEqualTo(CURRENT_OWNER_NAME);

        Map<String, Object> currentUserProps = githubApi.userClient().getUserProps();
        System.out.println("currentUserProps --> " + DumpUtils.dumpAsJsonTxt(currentUserProps));
        NavigableMap<String, Object> sortedUserProps = sortedMap(currentUserProps);
        System.out.println("sortedUserProps --> " + DumpUtils.dumpAsJsonTxt(sortedUserProps));
    }

    @Test
    void testOwner() {
        GithubApi.UserClient userClient = githubApi.userClient();
        GithubApi.User currentUser = userClient.getUser();
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.login()).isNotBlank();

        GithubApi.User currentOwner = userClient.getOwner(currentUser.login());
        System.out.println("currentUser --> " + DumpUtils.dumpAsJsonTxt(currentUser));
        System.out.println("currentOwner --> " + DumpUtils.dumpAsJsonTxt(currentOwner));
        assertThat(DumpUtils.dumpAsJsonTxt(currentOwner))
            .isEqualToNormalizingNewlines(DumpUtils.dumpAsJsonTxt(currentOwner));
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    void testRepositoryClient() {
        final String ownerName = "atteo";
        final String repoName = "classindex";
        GithubApi.RepositoryClient repoClient = githubApi.repositoryClient();
        GithubApi.Repository repo = repoClient.getRepository(ownerName, repoName);
        System.out.println("repo --> " + DumpUtils.dumpAsJsonTxt(repo));
        Map<String, Object> repoProps = repoClient.getRepoProps(ownerName, repoName);
        System.out.println("repoProps --> " + DumpUtils.dumpAsJsonTxt(repoProps));

        final String orgNameSlf4j = "qos-ch";
        Map<String, GithubApi.Repository> reposMap = githubApi.ownerReposMap(orgNameSlf4j);
        System.out.printf("list of repos of organization %s --> %s%n",
            orgNameSlf4j, DumpUtils.dumpAsJsonTxt(reposMap.keySet()));
        assertThat(reposMap).containsKeys("slf4j", "logback");

        Map<String, GithubApi.Repository> currentReposMap = githubApi.currentUserReposMap();
        assertThat(currentReposMap).containsKeys(CURRENT_REPO_NAME);
    }

    @Test
    void testCurrentRepo_NoOwner() {
        GithubApi githubApi_NoOwner = GithubApi.feignFactory()
            .githubToken(GithubHttpTest.GITHUB_AUTH_TOKEN)
            .create();
        assertThatIllegalStateException().isThrownBy(githubApi_NoOwner::getCurrentRepo)
            .withMessage("could not get the current repository, because there's no information about current repository-name in this instance of GithubApiFeign");
        assertThatIllegalStateException().isThrownBy(githubApi_NoOwner::currentRepoProps)
            .withMessage("could not get the properties of current repository, because there's no information about current repository-name in this instance of GithubApiFeign");
    }

    @Test
    void testCurrentRepo() {
        assertThat(githubApi.getCurrentRepo().name()).isEqualTo(CURRENT_REPO_NAME);
        Map<String, Object> currRepoProps = githubApi.currentRepoProps();
        System.out.println("currRepoProps --> " + DumpUtils.dumpAsJsonTxt(currRepoProps));
        assertThat(propValue(currRepoProps, "owner", "login")).isEqualTo(CURRENT_OWNER_NAME);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    void testPackageClient() {
        GithubApi.PackageClient packageClient = githubApi.packageClient();
        Collection<GithubApi.Package> userMavenPackages = packageClient.getUserMavenPackages();
        System.out.println("userMavenPackages --> " + DumpUtils.dumpAsJsonTxt(userMavenPackages));
        Collection<GithubApi.Package> ownerMavenPackages = packageClient.getOwnerMavenPackages(CURRENT_OWNER_NAME);
        System.out.println("ownerMavenPackages --> " + DumpUtils.dumpAsJsonTxt(ownerMavenPackages));
        assertThat(userMavenPackages).containsExactlyInAnyOrderElementsOf(ownerMavenPackages);
    }

    @Test
    void testMavenPackagesMap() {
        final String currentUserLogin = githubApi.getCurrentUser().login();
        Map<String, GithubApi.Package> usrPkgMap = githubApi.userMavenPackagesMap();
        Map<String, GithubApi.Package> ownPkgMap = githubApi.ownerMavenPackagesMap(currentUserLogin);
        assertThat(usrPkgMap).isEqualTo(ownPkgMap);
        assertThat(usrPkgMap).containsKey(CURRENT_GITHUB_PACKAGE_NAME);

        Collection<GithubApi.Package> usrPkgList = githubApi.packageClient().getUserMavenPackages();
        assertThat(usrPkgMap.values()).containsExactlyInAnyOrderElementsOf(usrPkgList);

        GithubApi spyGithubApi = Mockito.spy(githubApi);
        Mockito.doReturn(new GithubApi.PackageClient() {
            @Override
            public Collection<GithubApi.Package> getUserMavenPackages() {
                return usrPkgList;
            }
            @Override
            public Collection<GithubApi.Package> getOwnerMavenPackages(String ownerName) {
                // duplicated elements are expected to be ignored when the result map is collected
                return listTwiceOf(usrPkgList);
            }
        }).when(spyGithubApi).packageClient();
        assertThat(spyGithubApi.packageClient().getOwnerMavenPackages(currentUserLogin)).hasSize(2 * usrPkgMap.size());
        assertThat(spyGithubApi.ownerMavenPackagesMap(currentUserLogin)).hasSize(usrPkgMap.size());
    }

    @Test
    void testUserRepoToMavenPackages() {
        NavigableMap<String, NavigableMap<String, GithubApi.Package>> usrRepoToMvnPkg =
            githubApi.userRepoToMavenPackages();
        assertThat(usrRepoToMvnPkg).containsKey(CURRENT_REPO_NAME);
        assertThat(usrRepoToMvnPkg.get(CURRENT_REPO_NAME)).containsKey(CURRENT_GITHUB_PACKAGE_NAME);

        GithubApi spyGithubApi = Mockito.spy(githubApi);
        Mockito.doReturn(new GithubApi.PackageClient() {
            @Override
            public Collection<GithubApi.Package> getUserMavenPackages() {
                return mockPackages_noDups();
            }
            @Override
            public Collection<GithubApi.Package> getOwnerMavenPackages(String ownerName) {
                return fail("must not be invoked !!!");
            }
        }).when(spyGithubApi).packageClient();

        NavigableMap<String, NavigableMap<String, GithubApi.Package>> mockUsrRepoToMvnPkg =
            spyGithubApi.userRepoToMavenPackages();
        assertThat(DumpUtils.dumpAsJsonTxt(mockUsrRepoToMvnPkg))
            .isEqualToNormalizingNewlines(
                resourceAsString("mock-repo-to-mvn-pkg--no-duplicates.json"));
    }

    @Test
    void testOwnerRepoToMavenPackages() {
        NavigableMap<String, NavigableMap<String, GithubApi.Package>> ownRepoToMvnPkg =
            githubApi.ownerRepoToMavenPackages(CURRENT_OWNER_NAME);
        assertThat(ownRepoToMvnPkg).containsKey(CURRENT_REPO_NAME);
        assertThat(ownRepoToMvnPkg.get(CURRENT_REPO_NAME)).containsKey(CURRENT_GITHUB_PACKAGE_NAME);

        GithubApi spyGithubApi = Mockito.spy(githubApi);
        Mockito.doReturn(new GithubApi.PackageClient() {
            @Override
            public Collection<GithubApi.Package> getUserMavenPackages() {
                return fail("must not be invoked !!!");
            }
            @Override
            public Collection<GithubApi.Package> getOwnerMavenPackages(String ownerName) {
                return mockPackages_withDups();
            }
        }).when(spyGithubApi).packageClient();

        NavigableMap<String, NavigableMap<String, GithubApi.Package>> mockOwnRepoToMvnPkg =
            spyGithubApi.ownerRepoToMavenPackages("ignored-oner-name");
        assertThat(DumpUtils.dumpAsJsonTxt(mockOwnRepoToMvnPkg))
            .isEqualToNormalizingNewlines(
                resourceAsString("mock-repo-to-mvn-pkg--with-duplicates.json"));
    }

    @Test
    void testCurrentRepoMavenPkg() {
        assertThat(githubApi.isCurrentRepoMavenPkgPresent()).isTrue();
        GithubApi.Package currentPkg = githubApi.getCurrentRepoMavenPkg();
        assertThat(currentPkg).isNotNull();
        assertThat(currentPkg.name()).isEqualTo(CURRENT_GITHUB_PACKAGE_NAME);
        // subsequent access to current package must return the reference to the same object
        assertThat(githubApi.getCurrentRepoMavenPkg()).isSameAs(currentPkg);
        assertThat(githubApi.getCurrentRepoMavenPkg()).isSameAs(currentPkg);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    void testPkgVerClient() {
        Collection<GithubApi.PkgVer> usrPkgVerColl =
            githubApi.pkgVerClient().getUserMavenPackageVersions(CURRENT_GITHUB_PACKAGE_NAME);
        //System.out.println("usrPkgVerColl --> " + DumpUtils.dumpAsJsonTxt(usrPkgVerColl));
        Collection<GithubApi.PkgVer> ownPkgVerColl =
            githubApi.pkgVerClient().getOwnerMavenPackageVersions(CURRENT_OWNER_NAME, CURRENT_GITHUB_PACKAGE_NAME);
        //System.out.println("ownPkgVerColl --> " + DumpUtils.dumpAsJsonTxt(usrPkgVerColl));
        System.out.println("==============================================");
        System.out.println("usrPkgVerColl.size() = " + usrPkgVerColl.size());
        System.out.println("ownPkgVerColl.size() = " + ownPkgVerColl.size());
        System.out.println("==============================================");
        Map<String, Object> queryMap = linkedMap(
            nameValue("page", "1"),
            nameValue("per_page", "40")
        );
        PagingResult<GithubApi.PkgVer> usrPkgVerResult =
            githubApi.pkgVerClient().userMavenPackageVersions(
                CURRENT_GITHUB_PACKAGE_NAME, queryMap);
        System.out.println("usrPkgVerResult --> " + usrPkgVerResult);
        assertThat(usrPkgVerResult.itemsList()).hasSize(40);
        assertThat(usrPkgVerResult.nextPage()).isEqualTo(2);
        assertThat(usrPkgVerResult.lastPage()).isEqualTo(2);
    }

    @Test
    void testPkgVerClient_size() {
        Map<String, Object> queryMap = linkedMap(
            nameValue("page", "1"),
            nameValue("per_page", "1")
        );
        PagingResult<GithubApi.PkgVer> usrPkgVerResult =
            githubApi.pkgVerClient().userMavenPackageVersions(
                CURRENT_GITHUB_PACKAGE_NAME, queryMap);
        System.out.println("usrPkgVerResult --> " + usrPkgVerResult);
        assertThat(usrPkgVerResult.itemsList()).hasSize(1);
        assertThat(usrPkgVerResult.nextPage()).isEqualTo(2);
        assertThat(usrPkgVerResult.lastPage()).isEqualTo(58);
    }

    // ---------------------------------------------------------------------------------------------

    private static List<GithubApi.Package> mockPackages_noDups() {
        return List.of(
            mockPkg("one", "A", "01"),
            mockPkg("one", "A", "02"),
            mockPkg("two", "B", "04"),
            mockPkg("three", "C", "05"),
            mockPkg("three", "C", "06")
        );
    }

    private static List<GithubApi.Package> mockPackages_withDups() {
        return List.of(
            mockPkg("one", "A", "01"),
            mockPkg("one", "A", "02"),
            mockPkg("one", "B", "03"),  // <-- duplicated package name
            mockPkg("two", "B", "03"),  // <-- duplicated package name
            mockPkg("two", "B", "04"),
            mockPkg("three", "C", "05")
        );
    }

    private static GithubApi.Package mockPkg(String repoSuffix, String groupSuffix, String projectSuffix) {
        return new GithubApi.Package(
            "test-pkg-" + repoSuffix + "-" + projectSuffix + "-ID",
            "test-group-" + groupSuffix + ".test-artifact-" + projectSuffix,
            mockRepo(repoSuffix),
            "maven",
            123,
            "<< no HTML URL for GitHub-package >>",
            givenDaysAgo(35),
            givenDaysAgo(25)
        );
    }

    private static GithubApi.Repository mockRepo(String repoSuffix) {
        return new GithubApi.Repository(
            "test-repo-id-" + repoSuffix,
            "test-repo-name-" + repoSuffix,
            "some description of repo '" + repoSuffix + "'",
            "full-test-repo-name-" + repoSuffix,
            "<< no HTML URL for GitHub-repo >>",
            "<< no GIT URL for GitHub-repo >>",
            givenDaysAgo(20),
            givenDaysAgo(10),
            givenDaysAgo(5)
        );
    }

    private static LocalDateTime ldtBaseDateTime() {
        return LocalDateTime.of(2025, Month.NOVEMBER, 26, 12, 34);
    }

    private static DateTimeTriplet givenDaysAgo(int daysAgo) {
        return new DateTimeTriplet(ldtBaseDateTime().minusDays(daysAgo));
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressWarnings("SameParameterValue")
    private String resourceAsString(String resourceRelativePath) {
        try (InputStream resourceStream = getClass().getResourceAsStream(resourceRelativePath)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException(String.format(
                    "no resource by relative resource-path '%s'", resourceRelativePath));
            }
            return IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not load the resource by relative resource-path '%s'", resourceRelativePath), ioEx);
        }
    }
}
