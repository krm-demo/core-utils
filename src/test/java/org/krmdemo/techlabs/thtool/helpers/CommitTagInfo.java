package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the information about <b>{@code git}</b>-tag.
 * <hr/>
 * The most important things to remember that <b>{@code git}</b> supports two kind of tags:<ul>
 *     <li>
 *         A regular or <b>lightweight tag</b> (that does not have persistence state in repository),
 *         which us usually created from command-line as {@code git tag <tag-name>}.
 *         As for properties of given class - for lightweight tag the values {@link #tagID}
 *         and {@link #commitID} are the same and correspond to tagged <b>{@code git}</b>-commit,
 *         and properties, which are related to tag-message, are always {@code null}.
 *     </li>
 *     <li>
 *         An <b>annotated tag</b> (that has persistence state in repository),
 *         which us usually created from command-line as {@code git tag -a <tag-name> -m <annotated-message>}.
 *         As for annotated tags the values of {@link #tagID} and {@link #commitID} are different
 *         and in addition to that properties, which are related to tag-message, are not {@code null}.
 *     </li>
 * </ul>
 * As for properties of given class -
 *
 * @see <a href="https://git-scm.com/book/en/v2/Git-Basics-Tagging">
 *     2.6 Git Basics - Tagging
 * </a>
 */
@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommitTagInfo {

    final String tagID;
    final String tagName;
    final String commitID;

    @JsonIgnore private String messageFull;
    @JsonIgnore private String messageShort;
    @JsonIgnore private List<String> messageLines;

    private String taggerName;
    private String taggerEmail;

    CommitTagInfo(Repository repo, Ref tagRef) {
        this.tagName = tagRef.getName();
        String tagRefObjName = tagRef.getObjectId().getName();
        this.tagID = tagRefObjName;
        Ref peelRef = peelRef(repo, tagRef);
        if (peelRef == null || peelRef.getPeeledObjectId() == null) {
            this.commitID = tagRefObjName;
            return;
        }
        this.commitID = peelRef.getPeeledObjectId().getName();
        RevTag revTag = revTag(repo, tagRef, peelRef, this.tagName, this.tagID, this.commitID);
        if (revTag != null) {
            this.messageFull = revTag.getFullMessage();
            this.messageShort = revTag.getShortMessage();
            this.messageLines = this.messageFull.lines().toList();
            this.taggerName = revTag.getTaggerIdent().getName();
            this.taggerEmail = revTag.getTaggerIdent().getEmailAddress();
        }
    }

    @JsonGetter
    public Object getMessage() {
        if (messageLines == null || messageLines.size() > 1) {
            return messageLines;
        } else {
            return messageShort;
        }
    }

    private static RevTag revTag(Repository repo, Ref tagRef, Ref peelRef, String tagName, String tagID, String commitID) {
        try (RevWalk revWalk = new RevWalk(repo)) {
            return revWalk.parseTag(peelRef.getObjectId());
        } catch (IOException ioEx) {
            String errMsg = String.format("""
                "IOException while loading 'peelRef':
                - tagName = '%s';
                - tagRef -> %s;
                - peelRef -> %s;
                - tagRef.ObjectId -> %s;
                - peelRef.ObjectId -> %s;
                - tagID = '%s';
                - commitID = '%s';
                """, tagName,
                tagRef, peelRef,
                tagRef.getObjectId(), peelRef.getObjectId(),
                tagID, commitID
            );
            log.error(errMsg, ioEx);
            return null;
        }
    }

    private static Ref peelRef(Repository repo, Ref tagRef) {
        try {
            return repo.getRefDatabase().peel(tagRef);
        } catch (IOException ioEx) {
            String errMsg = "IOException while loading 'peelRef' for tag - " + tagRef.getName();
            log.error(errMsg, ioEx);
            return null;
        }
    }

    @Override
    public final boolean equals(Object thatObj) {
        if (thatObj instanceof CommitTagInfo that) {
            return Objects.equals(this.tagName, that.tagName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagName);
    }
}
