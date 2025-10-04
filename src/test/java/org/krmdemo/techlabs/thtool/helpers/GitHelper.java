package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NavigableMap;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedSet;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;

/**
 * This Java-record represents a <b>{@code th-tool}</b>-helper to work with local <b>{@code git}</b>-repository.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code git}.
 * <hr/>
 * The implementation is based on <i>porcelain-API</i> of a library <b>JGit</b>.
 *
 * @see <a href="https://git-scm.com/book/en/v2">
 *     <code>git</code> - Book
 * </a>
 * @see <a href="https://git-scm.com/docs/git-log">
 *     <code>git log</code> - Show commit logs
 * </a>
 * @see <a href="https://git-scm.com/book/pl/v2/Appendix-B:-Embedding-Git-in-your-Applications-JGit">
 *     <b>JGit</b> - Embedding Git in your Applications
 * </a>
 * @see <a href="https://www.baeldung.com/jgit">
 *     (Baeldung) A Guide to <b>JGit</b>
 * </a>
 * @see <a href="https://wiki.eclipse.org/JGit/User_Guide">
 *     <b>JGit</b> / User Guide
 * </a>
 *
 * @param gitRepoDir a directory at local file-system that represents a local git-repository
 */
public record GitHelper(File gitRepoDir) {

    public static final String VAR_NAME__HELPER = "git";

    public static GitHelper fromCtx(ThymeleafToolCtx ttCtx) {
        GitHelper helper = ttCtx.typedVar(VAR_NAME__HELPER, GitHelper.class);
        if (helper == null) {
            register(ttCtx);
            helper = ttCtx.typedVar(VAR_NAME__HELPER, GitHelper.class);
        }
        return helper;
    }

    public static void register(ThymeleafToolCtx ttCtx) {
        ttCtx.setVariable(VAR_NAME__HELPER, new GitHelper());
    }

    public GitHelper() {
        // at most file-systems the dot "." means the current working-directory:
        this(".");
    }

    public GitHelper(String girRepoDirPathStr) {
        this(Paths.get(girRepoDirPathStr));
    }

    public GitHelper(Path gitRepoDirPath) {
        this(gitRepoDirPath.toFile());
    }

    /**
     * Getting the map of remote local-names (like {@code 'origin'}) to remote URLs
     * <hr/>
     * P.S.: useful git-CLI command is also {@code git config --list --show-origin}
     *
     * @return similar to what {@code git remote -v} returns
     */
    public NavigableMap<String,String> getRemoteUrls() {
        try (Git git = Git.open(gitRepoDir)) {
            Repository repo = git.getRepository();
            StoredConfig config = repo.getConfig();
//            System.out.println("remoteNames = " + repo.getRemoteNames());
//            System.out.println("remoteName[origin] = " + repo.getRemoteName("origin"));
            return repo.getRemoteNames().stream().collect(toSortedMap(
                Function.identity(),
                remoteName -> config.getString("remote", remoteName, "url")
            ));
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not get the remote-names of a local git-repository '%s'",
                gitRepoDir.getPath()), ioEx);
        }
    }

    /**
     * @return similar to what {@code git status} returns
     */
    public Status getGitStatus() {
        try (Git git = Git.open(gitRepoDir)) {
            return git.status().call();
        } catch (IOException | GitAPIException | NoWorkTreeException gitEx) {
            throw new IllegalStateException(String.format(
                "Could not get the status of a local git-repository '%s'",
                gitRepoDir.getPath()), gitEx);
        }
    }

    /**
     * @return similar to what {@code git tag} returns
     */
    @JsonIgnore
    public SequencedSet<VersionTag> getVersionTags() {
        return getGitLog().values().stream()
            .filter(CommitInfo::hasVersionTag)
            .map(CommitInfo::getVersionTag)
            .collect(toLinkedSet());
    }

    @JsonIgnore
    public SequencedSet<CommitTagInfo> getTagInfos() {
        try (Git git = Git.open(gitRepoDir)) {
            Repository repo = git.getRepository();
            return repo.getRefDatabase().getRefsByPrefix(Constants.R_TAGS).stream()
                .map(tagRef -> new CommitTagInfo(repo, tagRef))
                .collect(toLinkedSet());
        } catch (IOException | NoWorkTreeException gitEx) {
            throw new IllegalStateException(String.format(
                "Could not get the linked-set of tags from a local git-repository '%s'",
                gitRepoDir.getPath()), gitEx);
        }
    }

    /**
     * @return similar to what {@code git log} returns
     */
    @JsonIgnore
    public SequencedMap<String, CommitInfo> getGitLog() {
        try (Git git = Git.open(gitRepoDir)) {
            Spliterator<RevCommit> revCommitsSI = git.log().call().spliterator();
            SequencedMap<String, CommitInfo> commitsMap = StreamSupport.stream(revCommitsSI, false)
                .map(CommitInfo::new)
                .collect(toLinkedMap(
                    CommitInfo::getCommitID,
                    Function.identity()
                ));
            Repository repo = git.getRepository();
            repo.getRefDatabase().getRefsByPrefix(Constants.R_TAGS).forEach(tagRef -> {
                CommitTagInfo cti = new CommitTagInfo(repo, tagRef);
                CommitInfo ci = commitsMap.get(cti.commitID);
                if (ci != null) {
                    ci.acceptTagInfo(cti);
                }
            });
            return commitsMap;
        } catch (IOException | GitAPIException | NoWorkTreeException gitEx) {
            throw new IllegalStateException(String.format(
                "Could not get the log of commits with tags from a local git-repository '%s'",
                gitRepoDir.getPath()), gitEx);
        }
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
