package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "major-group-info",
    "final-minor-group",
    "minor-groups"
})
public class CommitGroupMajor {

    private CommitGroupMinor currentMinor = new CommitGroupMinor();
    private final SequencedMap<VersionTag, CommitGroupMinor> minorGroupsMap = new LinkedHashMap<>();

    void acceptCommit(CommitInfo commitInfo) {
        if (isFinalized()) {
            throw new IllegalStateException("major commit-group is already finalized !!!");
        }
        currentMinor.acceptCommit(commitInfo);
        if (currentMinor.isFinalized() && !currentMinor.versionTag().isPublicRelease()) {
            minorGroupsMap.putFirst(currentMinor.versionTag(), currentMinor);
            currentMinor = new CommitGroupMinor();
        }
    }

    @JsonIgnore
    public boolean isEmpty() {
        return currentMinor.isEmpty() && minorGroupsMap.isEmpty();
    }

    @JsonIgnore
    public boolean isFinalized() {
        return versionTag() != null;
    }

    @JsonIgnore
    public VersionTag versionTag() {
        return currentMinor.versionTag();
    }

    @JsonIgnore
    public CommitGroupMinor currentMinor() {
        return currentMinor;
    }

    @JsonIgnore
    public CommitGroupMinor finalMinor() {
        return minorGroupsMap.isEmpty() ? null : minorGroupsMap.firstEntry().getValue();
    }

    @JsonIgnore
    public String finalDateTimeStr() {
        return minorGroupsMap.isEmpty() ? null : finalMinor().finalDateTimeStr();
    }

    @JsonGetter("major-group-info")
    public String getGroupInfo() {
        if (isEmpty()) {
            return "empty current major group";
        } else if (isFinalized()) {
            return String.format("%s (finalized major group with %d finalized minor groups)",
                versionTag(),
                minorGroupsMap.size()
            );
        } else if (currentMinor().isEmpty() && !minorGroupsMap.isEmpty()) {
            return String.format("%s (current major group with all %d minor groups finalized)",
                versionTag(),
                minorGroupsMap.size()
            );
        } else if (!currentMinor().isEmpty() && minorGroupsMap.isEmpty()) {
            return String.format("%s (open current major group without any finalized minor groups)",
                versionTag()
            );
        } else {
            return String.format("%s (open current major group with %d minor groups finalized)",
                versionTag(),
                minorGroupsMap.size()
            );
        }
    }

    @JsonGetter("final-minor-group")
    public CommitGroupMinor current() {
        return currentMinor.isEmpty() ? null : currentMinor;
    }

    @JsonGetter("minor-groups")
    public Collection<CommitGroupMinor> getMinorGroups() {
        return minorGroupsMap.values();
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsYamlTxt(this);
    }
}
