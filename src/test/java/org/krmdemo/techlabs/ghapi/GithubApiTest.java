package org.krmdemo.techlabs.ghapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        GithubApi.User currentUser = githubApi.currentUser();
        assertThat(currentUser).isNotNull();
        System.out.println("currentUser --> " + currentUser);
        assertThat(currentUser.login()).isEqualTo("krm-demo");
    }
}
