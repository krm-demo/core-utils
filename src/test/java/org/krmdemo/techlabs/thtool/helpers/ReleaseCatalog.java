package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

/**
 * This class represents the 2-level hierarchy of <b>{@code git}</b>-commits
 * based on sequences of INTERNAL-releases and PUBLIC-releases.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "current-snapshot-group",
    "current-minor-groups",
    "major-groups"
})
public class ReleaseCatalog {

    private CommitGroupMajor currentMajor = new CommitGroupMajor();
    private final SequencedMap<VersionTag, CommitGroupMajor> majorGroupsMap = new LinkedHashMap<>();

    public ReleaseCatalog(SequencedMap<String, CommitInfo> gitLog) {
        if (gitLog == null || gitLog.isEmpty()) {
            return;
        }
        gitLog.sequencedEntrySet().reversed().forEach(entry -> {
            currentMajor.acceptCommit(entry.getValue());
            if (!currentMajor.isFinalized()) {
                return;
            }
            majorGroupsMap.putFirst(currentMajor.versionTag(), currentMajor);
            currentMajor = new CommitGroupMajor();
        });
    }

    @JsonIgnore
    public CommitGroupMinor currentMinor() {
        return currentMajor.currentMinor();
    }

    @JsonIgnore
    public CommitGroupMinor finalMinor() {
        return currentMajor.finalMinor();
    }

    @JsonIgnore
    public CommitGroupMajor finalMajor() {
        return majorGroupsMap.isEmpty() ? null : majorGroupsMap.firstEntry().getValue();
    }

    @JsonGetter("current-snapshot-group")
    public CommitGroupMinor current() {
        return currentMajor.current();
    }

    @JsonGetter("current-minor-groups")
    public Collection<CommitGroupMinor> minorGroups() {
        return currentMajor.minorGroups();
    }

    @JsonGetter("major-groups")
    public Collection<CommitGroupMajor> majorGroups() {
        return majorGroupsMap.values();
    }

}
