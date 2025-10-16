package org.krmdemo.techlabs.thtool.helpers;

import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.datetime.LinkedDateTimeTripletMap;
import org.krmdemo.techlabs.core.datetime.LinkedDateTimeTripletMap.LinkedTriplet;

import java.util.SequencedMap;
import java.util.stream.Collectors;

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
            .collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String toString() {
        return dumpOneLine();
    }
}
