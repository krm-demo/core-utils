package org.krmdemo.techlabs.ghapi;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.krmdemo.techlabs.core.utils.CorePropsUtils.propValue;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedMap;

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
    static final String CURRENT_GITHUB_PACKAGE_NAME = String.format("%s.%s",
        CURRENT_MAVEN_PROJECT_GROUP, CURRENT_MAVEN_PROJECT_ARTIFACT);


    static GithubApi githubApi = GithubApi.feignFactory()
        .ownerName(CURRENT_OWNER_NAME)
        .repoName(CURRENT_REPO_NAME)
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
    }
}
