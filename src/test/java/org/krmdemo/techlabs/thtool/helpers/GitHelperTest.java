package org.krmdemo.techlabs.thtool.helpers;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Status;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test to <b>{@code th-tool}</b>-helper {@link GitHelper}.
 */
@Slf4j
public class GitHelperTest {

    GitHelper gitLogHelper = new GitHelper();

    @Test
    void testRemoteUrls() {
        System.out.println("remoteURLs = " + gitLogHelper.getRemoteUrls());
        assertThat(gitLogHelper.getRemoteUrls()).containsOnlyKeys("origin");
        assertThat(gitLogHelper.getRemoteUrls().get("origin"))
            .startsWith("https://github.com/krm-demo/core-utils");
    }

    @Test
    void testGitStatus() {
        Status gitStatus = gitLogHelper.getGitStatus();
        log.info("gitLogHelper.getGitStatus() --> " + DumpUtils.dumpAsJsonTxt(gitStatus));
        assertThat(gitStatus).isNotNull();
    }

    @Test
    void testGitTags() {
        gitLogHelper.getGitTags().forEach(ref -> {
            System.out.printf("- %s (peeled = %b; symbolic = %b) |%s|%n",
                ref.getName(),
                ref.isPeeled(), ref.isSymbolic(),
                ref.getObjectId().getName());
        });
    }

    @Test
    void testGitLog() {
        gitLogHelper.getGitLog().forEach(ci -> {
            System.out.printf("- %s \"%s\"%n", ci.getCommitID(), ci.getMessageFirstLine());
        });
    }



}
