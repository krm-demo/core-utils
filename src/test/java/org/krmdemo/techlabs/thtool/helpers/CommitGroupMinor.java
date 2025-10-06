package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "minor-group-info",
    "commits-one-line"
})
public class CommitGroupMinor {

    private final SequencedMap<String, CommitInfo> commitsMap = new LinkedHashMap<>();

    @JsonIgnore
    public boolean isEmpty() {
        return commitsMap.isEmpty();
    }

    @JsonIgnore
    public boolean isFinalized() {
        return versionTag() != null;
    }

    @JsonIgnore
    public VersionTag versionTag() {
        return isEmpty() ? null : finalCommit().versionTag;
    }

    @JsonIgnore
    public CommitInfo finalCommit() {
        return isEmpty() ? null : commitsMap.firstEntry().getValue();
    }

    @JsonIgnore
    public Collection<CommitInfo> commits() {
        return commitsMap.values();
    }

    @JsonGetter("minor-group-info")
    public String getGroupInfo() {
        if (!isFinalized()) {
            return String.format("<< unreleased >> %d commits", commitsMap.size());
        } else if (versionTag().isPublicRelease()) {
            if (commitsMap.size() > 1) {
                return String.format("<< PUBLIC %s >> %d working commits",
                    versionTag().toString(),
                    commitsMap.size() - 1
                );
            } else {
                return String.format("<< PUBLIC %s >> only one technical commit", versionTag());
            }
        } else {
            if (commitsMap.size() > 1) {
                return String.format("<< INTERNAL %s >> %d working commits",
                    versionTag().toString(),
                    commitsMap.size() - 1
                );
            } else {
                return String.format("<< INTERNAL %s >> only one technical commit", versionTag());
            }
        }
    }

    @JsonGetter("commits-one-line")
    public List<String> commitsOneLine() {
        return commits().stream().map(CommitInfo::dumpOneLine).toList();
    }

    void acceptCommit(CommitInfo commitInfo) {
        if (isFinalized()) {
            throw new IllegalStateException("minor commit-group is already finalized !!!");
        }
        commitsMap.putFirst(commitInfo.commitID, commitInfo);
    }
}
