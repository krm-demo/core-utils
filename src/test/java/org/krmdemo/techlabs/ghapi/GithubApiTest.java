package org.krmdemo.techlabs.ghapi;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Map;
import java.util.NavigableMap;

import static org.assertj.core.api.Assertions.assertThat;
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
        GithubApi.User currentUser = githubApi.currentUser();
        assertThat(currentUser).isNotNull();
        System.out.println("currentUser --> " + currentUser);
        assertThat(currentUser.login()).isEqualTo("krm-demo");

        Map<String, Object> currentUserProps = githubApi.userClient().getUserProps();
        System.out.println("currentUserProps --> " + DumpUtils.dumpAsJsonTxt(currentUserProps));
        NavigableMap<String, Object> sortedUserProps = sortedMap(currentUserProps);
        System.out.println("sortedUserProps --> " + DumpUtils.dumpAsJsonTxt(sortedUserProps));
    }
}
