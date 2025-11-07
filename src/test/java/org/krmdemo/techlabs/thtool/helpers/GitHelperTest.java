package org.krmdemo.techlabs.thtool.helpers;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Status;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsYamlTxt;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedMap;

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
        log.info(dumpAsYamlTxt(versionTags));
    }

    @Test
    void testTagInfos() {
        SequencedSet<CommitTagInfo> tagInfos = gitLogHelper.getTagInfos();
        log.info(String.format("%n--- testTagInfos(): %d version tags were found ---", tagInfos.size()));
        log.info(dumpAsYamlTxt(tagInfos));
    }

    @Test
    void testGitLog() {
        SequencedMap<String, CommitInfo> commitsMap = gitLogHelper.getGitLog();
        log.info(String.format("%n--- testGitLog(): %d version tags were found ---", commitsMap.size()));
        log.info(dumpAsYamlTxt(commitsMap));
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

    @Test
    void testCommitterSummary() {
        GitLogInfo gitLog = gitLogHelper.getGitLogInfo();
        Map<String, List<CommitInfo>> commitsByCommiterName =
            sortedMap(gitLog.commitsList().stream().collect(
                Collectors.groupingBy(CommitInfo::getCommitterName)));
        System.out.println("--------------  GitLogInfo by 'committerName' : ----------------");
        System.out.println("- total commits count: " + gitLog.commitsList().size() + "; ");
        System.out.println("- total committers count: " + commitsByCommiterName.size() + "; ");
        System.out.println("- all committers' names --> " + commitsByCommiterName.keySet() + "; ");
        for (String committerName : commitsByCommiterName.keySet()) {
            List<CommitInfo> commits = commitsByCommiterName.get(committerName);
            CommitInfo firstCommit = commits.getLast();  // git-log has reversed chronological order (the last is the first)
            CommitInfo lastCommit = commits.getFirst();  // git-log has reversed chronological order (the first is the last)
            System.out.printf("--** commiterName = '%s', commiterEmail = '%s', commitsCount = %d;%n",
                committerName, firstCommit.getCommitterEmail(), commits.size());
            System.out.println("-->  firstCommit dtt --> " + gitLog.linkedTriplet(firstCommit));
            System.out.printf( "--   firstCommit ID = '%s';%n", firstCommit.commitID);
            System.out.printf( "--   firstCommit msg = '%s';%n", firstCommit.getMessageShort());
            if (firstCommit.hasVersionTag()) {
                System.out.printf("--<  firstCommit VersionTag is '%s';%n", firstCommit.getVersionTag());
            }
            System.out.println("-->  lastCommit dtt --> " + gitLog.linkedTriplet(lastCommit));
            System.out.printf( "--   lastCommit ID = '%s';%n", lastCommit.commitID);
            System.out.printf( "--   lastCommit msg = '%s';%n", lastCommit.getMessageShort());
            if (lastCommit.hasVersionTag()) {
                System.out.printf("--<  lastCommit VersionTag is '%s';%n", lastCommit.getVersionTag());
            }
        }

        Map<Boolean, List<CommitInfo>> commitsByTechnical =
            gitLog.commitsList().stream().collect(
                Collectors.partitioningBy(CommitInfo::isTechnical));
        int countWorking = commitsByTechnical.get(Boolean.FALSE).size();
        int countTechnical = commitsByTechnical.get(Boolean.TRUE).size();
        assertThat(countWorking + countTechnical).isEqualTo(gitLog.commitsList().size());
        System.out.println("countWorking = " + countWorking);
        System.out.println("countTechnical = " + countTechnical);

        List<CommitInfo> technicalCommits = commitsByCommiterName.get(CommitInfo.TECH_COMMITTER_NAME);
        assertThat(technicalCommits.size()).isEqualTo(countTechnical);
    }
}
