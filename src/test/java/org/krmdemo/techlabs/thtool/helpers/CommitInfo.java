package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This class represents the information about git-commit.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommitInfo {

    final int commitTime;
    final String commitID;

    @JsonIgnore final String messageFull;
    @JsonIgnore final String messageShort;
    @JsonIgnore final List<String> messageLines;

    @JsonIgnore final String authorName;
    @JsonIgnore final String authorEmail;
    final String committerName;
    final String committerEmail;

    CommitTagInfo tagInfo;
    VersionTag versionTag;
    NavigableMap<String, CommitTagInfo> tagInfoAll = new TreeMap<>();

    CommitInfo(RevCommit revCommit) {
        this.commitTime = revCommit.getCommitTime();
        this.commitID = revCommit.getId().getName();

        this.messageFull = revCommit.getFullMessage();
        this.messageShort = revCommit.getShortMessage();
        this.messageLines = this.messageFull.lines().toList();

        this.authorName = revCommit.getAuthorIdent().getName();
        this.authorEmail = revCommit.getAuthorIdent().getEmailAddress();
        this.committerName = revCommit.getCommitterIdent().getName();
        this.committerEmail = revCommit.getCommitterIdent().getEmailAddress();
    }

    @JsonGetter("hasInfoTag")
    public boolean hasInfoTag() {
        return tagInfo != null;
    }

    @JsonGetter("hasVersionTag")
    public boolean hasVersionTag() {
        return versionTag != null && versionTag.isValid();
    }

    public boolean isPublicRelease() {
        return versionTag != null && versionTag.isPublicRelease();
    }

    @JsonGetter
    public Object getMessage() {
        if (messageLines.size() > 1) {
            return messageLines;
        } else {
            return messageShort;
        }
    }

    public void acceptTagInfo(CommitTagInfo tagInfo) {
        this.tagInfoAll.put(tagInfo.tagName, tagInfo);
        VersionTag versionTag = VersionTag.parse(tagInfo.tagName);
        if (versionTag != null && versionTag.isValid()) {
            this.versionTag = versionTag;
            this.tagInfo = tagInfo;
        } else if (this.tagInfo == null) {
            this.tagInfo = tagInfo;
        }
    }

    public String dumpOneLine() {
        return String.format("%6s | %d |  %s",
            commitID, commitTime, messageShort);
    }

    @Override
    public final boolean equals(Object thatObj) {
        if (!(thatObj instanceof CommitInfo that)) {
            return false;
        } else {
            return Objects.equals(this.commitID, that.commitID);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commitID);
    }
}
