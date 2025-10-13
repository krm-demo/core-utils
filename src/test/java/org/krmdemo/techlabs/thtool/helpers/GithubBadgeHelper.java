package org.krmdemo.techlabs.thtool.helpers;

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
    private final static String LABEL_COLOR__JAVADOC_NAVBAR = "4D7A97";
    private final static String LOGO_COLOR__JAVADOC_SELECTED = "f8981d";

    /**
     * @return the GitHub-Markdown'-badge to 'Release Catalog' (to be inserted at 'README.md')
     */
    public String getBadgeReleaseCatalog() {
        return String.format(
            "[![Release-Catalog](%s)](https://krm-demo.github.io/core-utils/)",
            getBadgeUrlReleaseCatalog());
    }

    /**
     * @return the URL to the badge of 'Release Catalog'
     */
    public String getBadgeUrlReleaseCatalog() {
        return badgeUrlShiedsIO("Release Catalog", null, LABEL_COLOR__JAVADOC_NAVBAR,
            LOGO_SLUG_NAME__GIT_HUB, LOGO_COLOR__JAVADOC_SELECTED, LABEL_COLOR__JAVADOC_NAVBAR);
    }

    /**
     * Returning the URL to the static-badge, which will be rendered by
     * <a href="https://shields.io/badges/static-badge">Shields IO</a>
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
                                 String logoSlugName, String colorLogo, String colorLabel) {
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

// https://img.shields.io/badge/lleft--part-right--part-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97
    // --------------------------------------------------------------------------------------------

    public String githubCommitUrl(CommitInfo commitInfo) {
        return githubCommitUrl(commitInfo.commitID);
    }

    public String githubCommitUrl(String commitID) {
        String repoUrl =  GithubHelper.fromCtx(ttCtx).getProjectRepoHtmlUrl();
        return String.format("%s/commit/%s", repoUrl, commitID);
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
