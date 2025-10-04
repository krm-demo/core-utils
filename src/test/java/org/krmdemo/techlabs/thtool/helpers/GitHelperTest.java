package org.krmdemo.techlabs.thtool.helpers;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Status;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Map;
import java.util.SequencedSet;

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
    void testVersionTags() {
        SequencedSet<VersionTag> versionTags = gitLogHelper.getVersionTags();
        System.out.printf("--- testVersionTags(): %d version tags were found ---%n", versionTags.size());
        System.out.println(DumpUtils.dumpAsYamlTxt(versionTags));
    }

    @Test
    void testGitLog() {
        Map<String, CommitInfo> commitsMap = gitLogHelper.getGitLog();
        System.out.println("commitsMap.size = " + commitsMap.size());
        System.out.println(DumpUtils.dumpAsYamlTxt(commitsMap));
    }
}
