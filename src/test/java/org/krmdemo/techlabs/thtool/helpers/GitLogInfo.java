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
                commitDateTimeFormatted(entry.getKey()),
                entry.getKey(),
                entry.getValue().getMessageShort()))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    // TODO: incapsualte that into core-utils somehow !!!
    private CharSequence commitDateTimeFormatted(String commitID) {
        LinkedTriplet linkedTriplet = linkedTripletsMap.get(commitID);
        StringBuilder sb = new StringBuilder();
        // the first part of date-time triplet:
        if (linkedTriplet.isYearAnMonthTheSameAsPrev()) {
            sb.append(" ".repeat(7));
        } else {
            sb.append(linkedTriplet.getYearAndMonth());
        }
        // the second part of date-time triplet:
        if (linkedTriplet.isDayOfMonthAndWeekTheSameAsPrev()) {
            sb.append(" ".repeat(10));
        } else if (StringUtils.isBlank(sb)) {
            // following manipulation are about to delete annoying dash('-')-symbol
            sb.append(' ');
            sb.append(linkedTriplet.getDayOfMonthAndWeek().substring(1));
        } else {
            sb.append(linkedTriplet.getDayOfMonthAndWeek());
        }
        // the third part of date-time triplet:
        if (linkedTriplet.isHoursMinutesTheSameAsPrev()) {
            sb.delete(0, sb.length());
            sb.append(String.format("%22s", "--- same minute  ---  "));
        } else {
            sb.append(linkedTriplet.getHoursMinutes());
        }
        return sb;
    }

    @Override
    public String toString() {
        return dumpOneLine();
    }
}
