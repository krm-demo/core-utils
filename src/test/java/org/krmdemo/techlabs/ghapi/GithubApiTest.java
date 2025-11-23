package org.krmdemo.techlabs.ghapi;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

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

    static GithubApi githubApi = GithubApi.feignFactory()
        .ownerName("krm-demo")
        .repoName("core-utils")
        .githubToken(GithubHttpTest.GITHUB_AUTH_TOKEN)
        .create();

    @Test
    void testCurrentUser() {
        GithubApi.User currentUser = githubApi.getCurrentUser();
        assertThat(currentUser).isNotNull();
        System.out.println("currentUser --> " + currentUser);
        assertThat(currentUser.login()).isEqualTo("krm-demo");

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
        assertThat(currentReposMap).containsKeys("core-utils");
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
        assertThat(githubApi.getCurrentRepo().name()).isEqualTo("core-utils");
        Map<String, Object> currRepoProps = githubApi.currentRepoProps();
        System.out.println("currRepoProps --> " + DumpUtils.dumpAsJsonTxt(currRepoProps));
        assertThat(propValue(currRepoProps, "owner", "login")).isEqualTo("krm-demo");
    }
}
