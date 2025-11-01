package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import static org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils.systemZoneOffset;

/**
 * This class represents the information about <b>{@code git}</b>-commit
 * and appears to be a light-weight wrapper (not holding the instance) over {@link RevCommit} from
 * <a href="https://wiki.eclipse.org/JGit/User_Guide"><b>JGit</b></a>-library
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommitInfo {

    /**
     * The number of hexadecimal digits of <b>{@code git}</b>-commit hash to display
     */
    public static final int SHORT_COMMIT_HASH_LENGTH = 7;

    /**
     * The value of {@link #committerName} for technical-commit
     * (that was performed by GitHUb-workflow - not by real person or tool manually)
     */
    public static final String TECH_COMMITTER_NAME = "github-actions";

    /**
     * The value of {@link #committerEmail} for technical-commit
     * (that was performed by GitHUb-workflow - not by real person or tool manually)
     */
    public static final String TECH_COMMITTER_EMAIL = "github-actions@github.com";

    /**
     * The name of CSS-class that is expected to be rendered for technical commits
     */
    public static final String TECH_CSS_NAME = "technical-commit";

    /**
     * The UTC epoch-seconds of the time when <b>{@code git}</b>-commit was made
     *
     * @see <a href="https://en.wikipedia.org/wiki/Unix_time">Unix (epoch) time</a>
     */
    final int commitTime;

    /**
     * 40-character SHA-1 hash of <b>{@code git}</b>-commit.
     *
     * @see <a href="https://git-scm.com/book/en/v2/Git-Tools-Revision-Selection">
     *     7.1 Git Tools - Revision Selection
     * </a>
     */
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

    /**
     * @return the short-value (of length {@link #SHORT_COMMIT_HASH_LENGTH} of {@link #commitID}
     */
    @JsonGetter("short-commit-hash")
    public String getShortCommitHash() {
        return commitID.substring(0, SHORT_COMMIT_HASH_LENGTH);
    }

    @JsonGetter("hasTag")
    public boolean hasTag() {
        return tagInfo != null;
    }

    @JsonGetter("hasVersionTag")
    public boolean hasVersionTag() {
        return versionTag != null && versionTag.isValid();
    }

    public boolean isPublicRelease() {
        return versionTag != null && versionTag.isPublicRelease();
    }

    /**
     * @return {@code true}, if corresponding commit was made by GitHub-workflow as a result of PUBLIC-release or INTERNAL-release
     */
    public boolean isTechnical() {
        return TECH_COMMITTER_NAME.equalsIgnoreCase(committerName)
            && TECH_COMMITTER_EMAIL.equalsIgnoreCase(committerEmail);
    }

    /**
     * @return the name of CSS-class if this commit {@link #isTechnical() is technical} or otherwise - empty string
     */
    public String getTechnicalCSS() {
        return isTechnical() ? TECH_CSS_NAME : "";
    }

    @JsonGetter
    public Object getMessage() {
        if (messageLines.size() > 1) {
            return messageLines;
        } else {
            return messageShort;
        }
    }

    @JsonGetter("local-date-time")
    public String localCommitTimeStr() {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(commitTime, 0, systemZoneOffset());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd EEE HH:mm:ss");
        return dtf.format(ldt);
    }

    public String dumpOneLine() {
        return String.format("%6s | %s |  %s",
            getShortCommitHash(),
            localCommitTimeStr(),
            messageShort
        );
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

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
