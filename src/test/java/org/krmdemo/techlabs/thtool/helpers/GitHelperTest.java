package org.krmdemo.techlabs.thtool.helpers;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Status;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.SequencedMap;
import java.util.SequencedSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link GitHelper}.
 */
@Slf4j
public class GitHelperTest {

    GitHelper gitLogHelper = new GitHelper();

    @Test
    void testRemoteUrls() {
        log.info("remoteURLs = " + gitLogHelper.getRemoteUrls());
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
        log.info(String.format("%n--- testVersionTags(): %d version-tags were found ---", versionTags.size()));
        log.info(DumpUtils.dumpAsYamlTxt(versionTags));
    }

    @Test
    void testTagInfos() {
        SequencedSet<CommitTagInfo> tagInfos = gitLogHelper.getTagInfos();
        log.info(String.format("%n--- testTagInfos(): %d version tags were found ---", tagInfos.size()));
        log.info(DumpUtils.dumpAsYamlTxt(tagInfos));
    }

    @Test
    void testGitLog() {
        SequencedMap<String, CommitInfo> commitsMap = gitLogHelper.getGitLog();
        log.info(String.format("%n--- testGitLog(): %d version tags were found ---", commitsMap.size()));
        log.info(DumpUtils.dumpAsYamlTxt(commitsMap));
    }

    @Test
    void testGitLogInfo() {
        log.info("--------------  GitLogInfo: ----------------");
        log.info("" + gitLogHelper.getGitLogInfo());
    }

    @Test
    void testReleaseCatalog() {
        log.info("--------------  Release Catalog: ----------------");
        log.info("" + gitLogHelper.getReleaseCatalog());
    }
}
