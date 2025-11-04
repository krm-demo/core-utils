package org.krmdemo.techlabs.thtool.helpers;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * Helper/utility class to provide mock-objects that are related to <b>JGit</b> porcelain-API
 *
 * @see <a href="https://git-scm.com/book/pl/v2/Appendix-B:-Embedding-Git-in-your-Applications-JGit">
 *     <b>JGit</b> - Embedding Git in your Applications
 * </a>
 */
public class MockRevCommitUtils {

    static CommitGroupMajor majorGroup(String versionStr) {
        return new CommitGroupMajor(() -> minorGroup(versionStr));
    }

    static CommitGroupMinor minorGroup(String versionStr) {
        return new CommitGroupMinor() {
            @Override
            public VersionTag versionTag() {
                return VersionTag.parse(versionStr);
            }
        };
    }

    @SuppressWarnings("SameParameterValue")
    static RevCommit mockRevCommit(String commitID) {
        return mockRevCommit(commitID,
            String.format("some test-message for mock-commid #%s",  commitID.substring(0, 7)));
    }

    static RevCommit mockRevCommit(String commitID, String message) {
        ObjectId mockObjectId = Mockito.mock(ObjectId.class);
        when(mockObjectId.getName()).thenReturn(commitID);

        PersonIdent authorIdent = Mockito.mock(PersonIdent.class);
        when(authorIdent.getName()).thenReturn("test-author");
        when(authorIdent.getEmailAddress()).thenReturn("test.author@junit5.com");
        PersonIdent commiterIdent = Mockito.mock(PersonIdent.class);
        when(commiterIdent.getName()).thenReturn("test-commiter");
        when(commiterIdent.getEmailAddress()).thenReturn("test.commiter@junit5.com");

        RevCommit mockRevCommit = Mockito.mock(RevCommit.class);
        when(mockRevCommit.getAuthorIdent()).thenReturn(authorIdent);
        when(mockRevCommit.getCommitterIdent()).thenReturn(commiterIdent);
        when(mockRevCommit.getShortMessage()).thenReturn(message);
        when(mockRevCommit.getFullMessage()).thenReturn(message);
        when(mockRevCommit.getId()).thenReturn(mockObjectId);
        return mockRevCommit;
    }
}
