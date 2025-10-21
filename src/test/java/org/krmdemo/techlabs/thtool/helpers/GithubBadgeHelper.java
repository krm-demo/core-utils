package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to render the individual badge and the whole <i>Badge Line</i>.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code gbh}.
 *
 * @see <a href="https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/about-writing-and-formatting-on-github">
 *     GitHub Flavored Markdown
 * </a>
 * @see <a href="https://shields.io/badges">
 *     (Shields.io) Static Badge
 * </a>
 * @see <a href="https://github.com/simple-icons/simple-icons/blob/master/slugs.md">
 *     Simple Icons slugs
 * </a>
 * @see <a href="https://img.shields.io/badge/lleft--part-right--part-blue?logo=github&logoColor=red&labelColor=green">
 *     an example of badge <br/>
 *     (git-hub-logo|left-part|right-part)
 * </a>
 */
@JsonPropertyOrder(alphabetic = true)
public class GithubBadgeHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link GithubBadgeHelper}
     */
    public static final String VAR_NAME__HELPER = "gbh";

    /**
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link GithubBadgeHelper} for unit-tests
     */
    public static GithubBadgeHelper fromCtxLazy(ThymeleafToolCtx ttCtx) {
        GithubBadgeHelper helper = fromCtx(ttCtx);
        if (helper == null) {
            GithubBadgeHelper.register(ttCtx);
            GithubHelper.register(ttCtx);
            GitHelper.register(ttCtx);
            MavenHelper.register(ttCtx);
            helper = fromCtx(ttCtx);
        }
        return helper;
    }

    /**
     * A factory-method that returns an instance of {@link GithubBadgeHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link GithubBadgeHelper} for access from other helpers
     */
    public static GithubBadgeHelper fromCtx(ThymeleafToolCtx ttCtx) {
        return ttCtx.typedVar(VAR_NAME__HELPER, GithubBadgeHelper.class);
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependency from <b>{@code th-tool}</b> to helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        GithubBadgeHelper helper = new GithubBadgeHelper(ttCtx);
        ttCtx.setVariable(VAR_NAME__HELPER, helper);
    }

    private final ThymeleafToolCtx ttCtx;
    private GithubBadgeHelper(ThymeleafToolCtx ttCtx) {
        this.ttCtx = Objects.requireNonNull(ttCtx);
    }

    // --------------------------------------------------------------------------------------------

    private final static String LOGO_SLUG_NAME__GIT_HUB = "github";
    private final static String LABEL_COLOR__VERSION = "blue";
    private final static String LABEL_COLOR__JAVADOC_NAVBAR = "4D7A97";
    private final static String LABEL_COLOR__GITHUB = "black";
    private final static String LOGO_COLOR__JAVADOC_SELECTED = "f8981d";
    private final static String LOGO_COLOR__GITHUB = "white";

    /**
     * @return the GitHub-Markdown'-badge to 'Release Catalog' (to be inserted at 'README.md')
     */
    public String getBadgeReleaseCatalogMD() {
        return String.format(
            "[![Release-Catalog](%s)](https://krm-demo.github.io/core-utils/)",
            getBadgeUrlReleaseCatalog());
    }

    /**
     * @return the HTML-badge to 'Release Catalog' (to be inserted at each HTML-page in processed JavaDoc-report)
     */
    public String getBadgeReleaseCatalogHTML() {
        return String.format("""
            <a href="https://krm-demo.github.io/core-utils/" class="release-catalog-badge-link">
              <img alt="a badge to 'Release Catalog'" src="%s" class="release-catalog-badge"/>
            </a>""",
            getBadgeUrlReleaseCatalog());
    }

    /**
     * @return the URL to the badge of 'Release Catalog'
     */
    public String getBadgeUrlReleaseCatalog() {
        return badgeUrlShiedsIO("Release Catalog", null, LABEL_COLOR__JAVADOC_NAVBAR,
            LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * This property is used at 'th-test-site', where standard JavaDoc 'Overview' link is not present.
     *
     * @return the HTML-badge that corresponds to the current JavaDoc-home page ('Overview' link, but with version-info)
     */
    @JsonIgnore
    public String getBadgeHomeJavaDocHTML() {
        return StringUtils.firstNonBlank(
            getBadgeSnapshotJavaDocHTML(),
            getBadgeLatestInternalJavaDocHTML(),
            getBadgeLatestPublicJavaDocHTML()
        );
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return {@code true} if the latest PUBLIC-release is available for this project, or {@code false} - otherwise
     */
    public boolean isLatestPublicAvailable() {
        return releaseCatalog().getFinalMajor() != null;
    }

    /**
     * @return the version to the latest PUBLIC-release or empty string if it's not available
     */
    public String getLatestPublicVersion() {
        if (!isLatestPublicAvailable()) {
            return "";
        } else if (isMavenInternal()) {
            return "" + releaseCatalog().getFinalMajor().versionTag();
        } else {
            return MavenHelper.fromCtx(ttCtx).getPublicReleaseVersion();
        }
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest PUBLIC-release JavaDoc (to be inserted at 'README.md')
     */
    public String getBadgeLatestPublicJavaDocMD() {
        return !isLatestPublicAvailable() ? "" :
            String.format(
                "[![Latest-Public](%s)](https://krm-demo.github.io/core-utils/%s-%s)",
                getBadgeUrlLatestPublicJavaDoc(), repoName(), getLatestPublicVersion());
    }

    /**
     * @return the HTML-badge to the latest PUBLIC-release JavaDoc (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeLatestPublicJavaDocHTML() {
        return !isLatestPublicAvailable() ? "" :
            String.format("""
                <a href="https://krm-demo.github.io/core-utils/%s-%s">
                  <img alt="a badge to the latest PUBLIC-version" src="%s" />
                </a>""",
                repoName(), getLatestPublicVersion(), getBadgeUrlLatestPublicJavaDoc());
    }

    /**
     * @return the URL to the latest PUBLIC-release JavaDoc
     */
    public String getBadgeUrlLatestPublicJavaDoc() {
        return !isLatestPublicAvailable() ? "" :
            badgeUrlShiedsIO(repoName(), getLatestPublicVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest PUBLIC-release project (to be inserted at 'README.md')
     */
    public String getBadgeLatestPublicGitHubMD() {
        return !isLatestPublicAvailable() ? "" :
            String.format(
                "[![Latest-Public](%s)](%s/tree/%s)",
                getBadgeUrlLatestPublicGitHub(), repoHtmlUrl(), getLatestPublicVersion());
    }

    /**
     * @return the HTML-badge to the latest PUBLIC-release project (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeLatestPublicGitHubHTML() {
        return !isLatestPublicAvailable() ? "" :
            String.format("""
                <a href="%s/tree/%s">
                  <img alt="a badge to the latest PUBLIC-version" src="%s" />
                </a>""",
                repoHtmlUrl(), getLatestPublicVersion(), getBadgeUrlLatestPublicGitHub());
    }

    // https://github.com/krm-demo/core-utils/tree/21.11
    // https://krm-demo.github.io/core-utils/core-utils-21.11/

    /**
     * @return the URL to the latest PUBLIC-release project
     */
    public String getBadgeUrlLatestPublicGitHub() {
        return !isLatestPublicAvailable() ? "" :
            badgeUrlShiedsIO(repoName(), getLatestPublicVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return {@code true} if the latest INTERNAL-release is available for this project, or {@code false} - otherwise
     */
    public boolean isLatestInternalAvailable() {
        return isMavenInternal() && (!isMavenSnapshot() || releaseCatalog().getFinalMinor() != null);
    }

    /**
     * @return the version of the latest INTERNAL-release or empty string if it's not available
     */
    public String getLatestInternalVersion() {
        if (!isLatestInternalAvailable()) {
            return "";
        } else if (!isMavenSnapshot()) {
            return MavenHelper.fromCtx(ttCtx).getInternalReleaseVersion();
        } else {
            return "" + releaseCatalog().getFinalMinor().versionTag();
        }
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest INTERNAL-release JavaDoc (to be inserted at 'README.md')
     */
    public String getBadgeLatestInternalJavaDocMD() {
        return !isLatestInternalAvailable() ? "" :
            String.format(
                "[![Latest-Internal](%s)](https://krm-demo.github.io/core-utils/%s-%s)",
                getBadgeUrlLatestInternalJavaDoc(), repoName(), getLatestInternalVersion());
    }

    /**
     * @return the HTML-badge to the latest INTERNAL-release JavaDoc (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeLatestInternalJavaDocHTML() {
        return !isLatestInternalAvailable() ? "" :
            String.format("""
                <a href="https://krm-demo.github.io/core-utils/%s-%s">
                  <img alt="a badge to the latest INTERNAL-version" src="%s" />
                </a>""",
                repoName(), getLatestInternalVersion(), getBadgeUrlLatestInternalJavaDoc());
    }

    /**
     * @return the URL to the badge to the latest INTERNAL-release JavaDoc
     */
    public String getBadgeUrlLatestInternalJavaDoc() {
        return !isLatestInternalAvailable() ? "" :
            badgeUrlShiedsIO(repoName(), getLatestInternalVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest PUBLIC-release project (to be inserted at 'README.md')
     */
    public String getBadgeLatestInternalGitHubMD() {
        return !isLatestInternalAvailable() ? "" :
            String.format(
                "[![Latest-Internal](%s)](%s/tree/%s)",
                getBadgeUrlLatestInternalGitHub(), repoHtmlUrl(), getLatestInternalVersion());
    }

    /**
     * @return the HTML-badge to the latest PUBLIC-release project (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeLatestInternalGitHubHTML() {
        return !isLatestInternalAvailable() ? "" :
            String.format("""
                <a href="%s/tree/%s">
                  <img alt="a badge to the latest PUBLIC-version" src="%s" />
                </a>""",
                repoHtmlUrl(), getLatestInternalVersion(), getBadgeUrlLatestInternalGitHub());
    }

    /**
     * @return the URL to the latest PUBLIC-release project
     */
    public String getBadgeUrlLatestInternalGitHub() {
        return !isLatestInternalAvailable() ? "" :
            badgeUrlShiedsIO(repoName(), getLatestInternalVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return the current version of maven-project via {@link MavenHelper#getCurrentProjectVersion()}
     */
    public String getSnapshotVersion() {
        return MavenHelper.fromCtx(ttCtx).getCurrentProjectVersion();
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest SNAPSHOT-version (to be inserted at 'README.md')
     */
    public String getBadgeSnapshotJavaDocMD() {
        return !isMavenSnapshot() ? "" :
            String.format(
                "[![Snapshot-Version](%s)](https://krm-demo.github.io/core-utils/%s-%s)",
                getBadgeUrlSnapshotJavaDoc(), repoName(), getSnapshotVersion());
    }

    /**
     * @return the HTML-badge to the latest SNAPSHOT-version (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeSnapshotJavaDocHTML() {
        return !isMavenSnapshot() ? "" :
            String.format("""
                <a href="https://krm-demo.github.io/core-utils/%s-%s">
                  <img alt="a badge to the latest SNAPSHOT-version" src="%s" />
                </a>""",
                repoName(), getSnapshotVersion(), getBadgeUrlSnapshotJavaDoc());
    }

    /**
     * @return the URL to the badge to the latest SNAPSHOT-version
     */
    public String getBadgeUrlSnapshotJavaDoc() {
        return !isMavenSnapshot() ? "" :
            badgeUrlShiedsIO(repoName(), getSnapshotVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * @return the GitHub-Markdown'-badge to the latest PUBLIC-release project (to be inserted at 'README.md')
     */
    public String getBadgeSnapshotGitHubMD() {
        return !isMavenSnapshot() ? "" :
            String.format(
                "[![Snapshot-Version](%s)](%s)",
                getBadgeUrlSnapshotGitHub(), repoHtmlUrl());
    }

    /**
     * @return the HTML-badge to the latest PUBLIC-release project (to be inserted at 'overview.html' and other places)
     */
    @JsonIgnore
    public String getBadgeSnapshotGitHubHTML() {
        return !isMavenSnapshot() ? "" :
            String.format("""
                <a href="%s">
                  <img alt="a badge to the latest PUBLIC-version" src="%s" />
                </a>""",
                repoHtmlUrl(), getBadgeUrlSnapshotGitHub());
    }

    /**
     * @return the URL to the latest PUBLIC-release project
     */
    public String getBadgeUrlSnapshotGitHub() {
        return !isMavenSnapshot() ? "" :
            badgeUrlShiedsIO(repoName(), getSnapshotVersion(), LABEL_COLOR__VERSION,
                LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return {@code true} if the maven-version has SNAPSHOT-qualifier for this project, or {@code false} - otherwise
     */
    public boolean isMavenSnapshot() {
        return MavenHelper.fromCtx(ttCtx).versionHasQualifierPart();
    }

    /**
     * @return {@code true} if the maven-version has incremental part for this project, or {@code false} - otherwise
     */
    public boolean isMavenInternal() {
        return MavenHelper.fromCtx(ttCtx).versionHasIncrementalPart();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Returning the URL to the static-badge, which will be rendered by
     * <a href="https://shields.io/badges/static-badge">Shields IO</a>.
     * <hr/>
     * This is <a href="https://img.shields.io/badge/left--part-right--part-blue?logo=github&logoColor=f8981d&labelColor=4D7A97">
     *     an example of such URL
     * </a>
     *
     * @param leftPart the left part of badge-name
     * @param rightPart the right-part of badge-name
     * @param colorPart the color of right-part background (or the whole badge-name background if one part is missing)
     * @param logoSlugName the logo-slug, provided by
     *                     <a href="https://github.com/simple-icons/simple-icons/blob/master/slugs.md">
     *                     Simple Icons
     *                     </a>
     * @param colorLogo the fill-color of logo-icon
     * @param colorLabel the background color of logo-icon
     * @return the URL to be inserted on your site
     */
    private String badgeUrlShiedsIO(String leftPart, String rightPart, String colorPart,
                                    @SuppressWarnings("SameParameterValue") String logoSlugName,
                                    String colorLogo, String colorLabel) {
        leftPart = escapeBadgeName(leftPart);
        rightPart = escapeBadgeName(rightPart);
        if (StringUtils.isNotBlank(leftPart) && StringUtils.isNotBlank(rightPart)) {
            return String.format("https://img.shields.io/badge/%s-%s-%s?logo=%s&logoColor=%s&labelColor=%s",
                leftPart, rightPart, colorPart, logoSlugName, colorLogo, colorLabel);
        }
        String singlePart = StringUtils.isNotBlank(leftPart) ? leftPart : rightPart;
        if (StringUtils.isBlank(singlePart)) {
            throw new IllegalArgumentException("either left and right part of badge name must be NOT blank");
        }
        return String.format("https://img.shields.io/badge/%s-%s?logo=%s&logoColor=%s&labelColor=%s",
            singlePart, colorPart, logoSlugName, colorLogo, colorLabel);
    }

    private static String escapeBadgeName(String namePart) {
        if (StringUtils.isBlank(namePart)) {
            return namePart;
        }
        namePart = namePart.replace("-", "--");
        namePart = namePart.replace("_", "__");
        namePart = namePart.replace(" ", "_");
        return namePart;
    }

    private ReleaseCatalog releaseCatalog() {
        return GitHelper.fromCtx(ttCtx).getReleaseCatalog();
    }

    private String repoName() {
        return GithubHelper.fromCtx(ttCtx).getRepoName();
    }

    private String repoHtmlUrl() {
        return GithubHelper.fromCtx(ttCtx).getProjectRepoHtmlUrl();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return HTML-badge to GitHub-project of the version that corresponds to the passed {@code commitGroupMinor}
     */
    @JsonIgnore
    public String badgeJavaDocHTML(CommitGroupMinor commitGroupMinor) {
        return String.format("""
            <a href="https://krm-demo.github.io/core-utils/%s-%s">
              <img alt="a badge to the GitHub-project '%s' of git-tag '%s'" src="%s" />
            </a>""",
            repoName(),
            versionStr(commitGroupMinor),
            repoName(),
            versionStr(commitGroupMinor),
            badgeUrlJavaDoc(commitGroupMinor));
    }

    /**
     * @return HTML-badge to GitHub-project of the version that corresponds to the passed {@code commitGroupMinor}
     */
    @JsonIgnore
    public String badgeJavaDocHTML(CommitGroupMajor commitGroupMajor) {
        return String.format("""
            <a href="https://krm-demo.github.io/core-utils/%s-%s">
              <img alt="a badge to the GitHub-project '%s' of git-tag '%s'" src="%s" />
            </a>""",
            repoName(),
            versionStr(commitGroupMajor),
            repoName(),
            versionStr(commitGroupMajor),
            badgeUrlJavaDoc(commitGroupMajor));
    }

    /**
     * @return the URL to the JavaDoc of GitHub project represented by passed {@code commitGroupMinor}
     */
    public String badgeUrlJavaDoc(CommitGroupMinor commitGroupMinor) {
        return badgeUrlShiedsIO(repoName(), versionStr(commitGroupMinor), LABEL_COLOR__VERSION,
            LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * @return the URL to the JavaDoc of GitHub project represented by passed {@code commitGroupMajor}
     */
    public String badgeUrlJavaDoc(CommitGroupMajor commitGroupMajor) {
        return badgeUrlShiedsIO(repoName(), versionStr(commitGroupMajor), LABEL_COLOR__VERSION,
            LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * @return HTML-badge to GitHub-project of the version that corresponds to the passed {@code commitGroupMinor}
     */
    @JsonIgnore
    public String badgeGitHubHTML(CommitGroupMinor commitGroupMinor) {
        return String.format("""
            <a href="%s/tree/%s">
              <img alt="a badge to the GitHub-project '%s' of git-tag '%s'" src="%s" />
            </a>""",
            repoHtmlUrl(),
            versionStr(commitGroupMinor),
            repoName(),
            versionStr(commitGroupMinor),
            badgeUrlGitHub(commitGroupMinor));
    }

    /**
     * @return HTML-badge to GitHub-project of the version that corresponds to the passed {@code commitGroupMinor}
     */
    @JsonIgnore
    public String badgeGitHubHTML(CommitGroupMajor commitGroupMajor) {
        return String.format("""
            <a href="%s/tree/%s">
              <img alt="a badge to the GitHub-project '%s' of git-tag '%s'" src="%s" />
            </a>""",
            repoHtmlUrl(),
            versionStr(commitGroupMajor),
            repoName(),
            versionStr(commitGroupMajor),
            badgeUrlGitHub(commitGroupMajor));
    }

    /**
     * @return the URL to the version of GitHub project represented by passed {@code commitGroupMinor}
     */
    public String badgeUrlGitHub(CommitGroupMinor commitGroupMinor) {
        return badgeUrlShiedsIO(repoName(), versionStr(commitGroupMinor),
            LABEL_COLOR__VERSION, LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    /**
     * @return the URL to the version of GitHub project represented by passed {@code commitGroupMajor}
     */
    public String badgeUrlGitHub(CommitGroupMajor commitGroupMajor) {
        return badgeUrlShiedsIO(repoName(), versionStr(commitGroupMajor),
            LABEL_COLOR__VERSION, LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    private String versionStr(CommitGroupMajor commitGroupMajor) {
        return "" + commitGroupMajor.versionTag();
    }

    private String versionStr(CommitGroupMinor commitGroupMinor) {
        return "" + commitGroupMinor.versionTag();
    }

    /**
     * @return the URL to the version of current GitHub project (at JavaDoc-report HTML-files)
     */
    public String badgeUrlGitHub() {
        return badgeUrlShiedsIO(repoName(), MavenHelper.fromCtx(ttCtx).getCurrentProjectVersion(),
            LABEL_COLOR__JAVADOC_NAVBAR, LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__GITHUB, LABEL_COLOR__GITHUB);
    }

    // --------------------------------------------------------------------------------------------

    public String badgeCommit(CommitInfo commitInfo) {
        return String.format("""
            <a href="%s">
              <svg aria-hidden="true" focusable="false"
                   viewBox="0 0 16 16" width="16" height="16"
                   stroke="#59636e" overflow="visible" style="vertical-align:text-bottom">
                <path d="M11.93 8.5a4.002 4.002 0 0 1-7.86 0H.75a.75.75 0 0 1 0-1.5h3.32a4.002 4.002 0 0 1 7.86 0h3.32a.75.75 0 0 1 0 1.5Zm-1.43-.75a2.5 2.5 0 1 0-5 0 2.5 2.5 0 0 0 5 0Z"></path>
              </svg>
              <code>%s</code>
            </a>""",
            githubCommitUrl(commitInfo),
            commitInfo.getShortCommitHash());
    }

    /**
     * This method is invoked from <b>{@code th-tool}</b>-template like "Release Catalog" via expression
     * <pre>{@code <a th:href="${gbh.githubCommitUrl(commitInfo)}"></a>}</pre>
     * in order to display the link to <b>{@code git}</b>-commit at the GitHub-site.
     *
     * @param commitInfo information about <b>{@code git}</b>-commit that is fetched by {@link GitHelper}
     * @return a URL reference to be rendered inside HTML-tag {@code <a href="...">...</a>}
     */
    @SuppressWarnings("unused")
    public String githubCommitUrl(CommitInfo commitInfo) {
        return githubCommitUrl(commitInfo.commitID);
    }

    public String githubCommitUrl(String commitID) {
        return String.format("%s/commit/%s", repoHtmlUrl(), commitID);
    }

    /**
     * @return the GitHub-Markdown'-badge to the results of 'on-main-push' workflow (to be inserted at 'README.md')
     */
    public String getBadgeBuildPassing() {
        return workflowBadgeMarkdown("on-main-push");
    }

    public String workflowBadgeMarkdown(String workflowName) {
        return workflowBadgeMarkdown(workflowName, workflowName + ".yml");
    }

    /**
     * @return something like {@code [![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)}
     */
    private String workflowBadgeMarkdown(String workflowName, String workflowSourceName) {
        String repoUrl =  GithubHelper.fromCtx(ttCtx).getProjectRepoHtmlUrl();
        String workflowUrl = repoUrl + "/actions/workflows/" + workflowSourceName;
        return String.format("[![%s](%s/badge.svg?event=push)](%s)", workflowName, workflowUrl, workflowUrl);
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
