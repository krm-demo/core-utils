package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

/**
 * This class represents the 2-level hierarchy of <b>{@code git}</b>-commits
 * based on sequences of INTERNAL-releases and PUBLIC-releases,
 * which are organized as <b>{@code git}</b>-tags to concrete specific commits.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "current-snapshot-group",
    "current-minor-groups",
    "major-groups"
})
public class ReleaseCatalog extends GitLogInfo {

    private CommitGroupMajor currentMajor = new CommitGroupMajor();
    private final SequencedMap<VersionTag, CommitGroupMajor> majorGroupsMap = new LinkedHashMap<>();

    public ReleaseCatalog(SequencedMap<String, CommitInfo> commitsMap) {
        super(commitsMap);
        commitsMap.sequencedEntrySet().reversed().forEach(entry -> {
            currentMajor.acceptCommit(entry.getValue());
            if (!currentMajor.isFinalized()) {
                return;
            }
            majorGroupsMap.putFirst(currentMajor.versionTag(), currentMajor);
            currentMajor = new CommitGroupMajor();
        });
    }

    @JsonIgnore
    public CommitGroupMinor getSnapshotGroup() {
        return currentMajor.currentMinor();
    }

    @JsonIgnore
    public CommitGroupMinor getFinalMinor() {
        return currentMajor.finalMinor();
    }

    @JsonIgnore
    public CommitGroupMajor getFinalMajor() {
        return majorGroupsMap.isEmpty() ? null : majorGroupsMap.firstEntry().getValue();
    }

    @JsonGetter("current-snapshot-group")
    public CommitGroupMinor current() {
        return currentMajor.current();
    }

    @JsonGetter("current-minor-groups")
    public Collection<CommitGroupMinor> getMinorGroups() {
        return currentMajor.getMinorGroups();
    }

    @JsonGetter("major-groups")
    public Collection<CommitGroupMajor> getMajorGroups() {
        return majorGroupsMap.values();
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsYamlTxt(this);
    }
}
