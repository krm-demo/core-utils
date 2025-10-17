package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.krmdemo.techlabs.core.datetime.LinkedDateTimeTripletMap;
import org.krmdemo.techlabs.core.datetime.LinkedDateTimeTripletMap.LinkedTriplet;

import java.util.Collection;
import java.util.SequencedMap;

import static org.krmdemo.techlabs.core.utils.CoreStringUtils.multiLine;

/**
 * This class represents a plain sequence of <b>{@code git}</b>-commits.
 */
public class GitLogInfo {

    private final SequencedMap<String, CommitInfo> commitsMap;
    private final SequencedMap<String, LinkedTriplet> linkedTripletsMap;

    public GitLogInfo(SequencedMap<String, CommitInfo> commitsMap) {
        this.commitsMap = commitsMap;
        this.linkedTripletsMap =
            LinkedDateTimeTripletMap.createByEpochSecondsInt(
                commitsMap, CommitInfo::getCommitTime);
    }

    /**
     * Dump <b>{@code git}</b>-commits in a way similar to command-line statement:<pre>{code
     *      ...> git log --oneline}
     * </pre>
     *
     * @return the output of one-line formatted git-log as {@link String}
     */
    public String dumpOneLine() {
        return commitsMap.entrySet().stream()
            .map(entry -> String.format("|%s|%s: %s",
                linkedTripletsMap.get(entry.getKey()).dumpLinked(),
                entry.getKey(),
                entry.getValue().getMessageShort()))
            .collect(multiLine());
    }

    /**
     * @return sequence of <b>{@code git}</b>-commits as {@link Collection Collection&lt;CommitInfo&gt;}
     */
    @JsonIgnore
    Collection<CommitInfo> commitsList() {
        return commitsMap.values();
    }

    /**
     * @param commitID <b>{@code git}</b>-commit ID (40-character SHA-1 hash)
     * @return information about <b>{@code git}</b>-commit as {@link CommitInfo}
     */
    public CommitInfo get(String commitID) {
        return commitsMap.get(commitID);
    }

    /**
     * @param commitID <b>{@code git}</b>-commit ID (40-character SHA-1 hash)
     * @return linked data-time-triplet as {@link LinkedTriplet}
     */
    public LinkedTriplet linkedTriplet(String commitID) {
        return linkedTripletsMap.get(commitID);
    }

    /**
     * @param commitInfo an instance of {@link CommitInfo} to get {@link CommitInfo#commitID commitID}
     * @return linked data-time-triplet as {@link LinkedTriplet}
     */
    public LinkedTriplet linkedTriplet(CommitInfo commitInfo) {
        return linkedTripletsMap.get(commitInfo.getCommitID());
    }

    @Override
    public String toString() {
        return dumpOneLine();
    }
}
