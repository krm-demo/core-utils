package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommitInfo implements Consumer<Ref> {

    final int commitTime;
    final String commitID;

    @JsonIgnore final String messageFirstLine;
    @JsonIgnore final String messageFull;
    @JsonIgnore final String messageShort;
    @JsonIgnore final List<String> messageLines;

    final String authorName;
    final String authorEmail;
    final String committerName;
    final String committerEmail;

    VersionTag versionTag;
    NavigableSet<String> allTags = new TreeSet<>();

    CommitInfo(RevCommit revCommit) {
        this.commitTime = revCommit.getCommitTime();
        this.commitID = revCommit.getId().getName();

        this.messageFirstLine = revCommit.getFirstMessageLine();
        this.messageFull = revCommit.getFullMessage();
        this.messageShort = revCommit.getShortMessage();
        this.messageLines = this.messageFull.lines().toList();

        this.authorName = revCommit.getAuthorIdent().getName();
        this.authorEmail = revCommit.getAuthorIdent().getEmailAddress();
        this.committerName = revCommit.getCommitterIdent().getName();
        this.committerEmail = revCommit.getCommitterIdent().getEmailAddress();
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

    @Override
    public void accept(Ref tagRef) {
        String tagName = tagRef.getName();
        this.allTags.add(tagName);
        this.versionTag = VersionTag.parse(tagName);
    }

}
