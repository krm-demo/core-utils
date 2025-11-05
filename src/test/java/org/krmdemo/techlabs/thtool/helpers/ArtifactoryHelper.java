package org.krmdemo.techlabs.thtool.helpers;

import lombok.Getter;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.Objects;
import java.util.function.Consumer;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.krmdemo.techlabs.thtool.helpers.GithubBadgeHelper.LOGO_SLUG_NAME__GIT_HUB;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to work with "GitHub-Packages"-artifactory
 * (where the artifacts of INTERNAL-release are deployed) and "Maven-Central Repository"-artifactory
 * (where the artifacts of PUBLIC-release are deployed. This helper is responsible just for rendering
 * the proper links to those external resources per each version that corresponds to {@link VersionTag}.
 * <hr/>
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code ah}.
 *
 * @see <a href="https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages">
 *     GitHub Packages
 * </a>
 * @see <a href="https://central.sonatype.com/">
 *     Maven Central Repository
 * </a>
 */
public class ArtifactoryHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link ArtifactoryHelper}
     */
    public static final String VAR_NAME__HELPER = "ah";

    /**
     * The name of <i>GH-Package</i> that is used to distribute <b>core-utils</b>-library
     * via <a href="https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages">
     *     GitHub Packages
 *     </a>
     */
    public static final String GH_PACkAGE_NAME = "io.github.krm-demo.core-utils";

    /**
     * The ID of <i>GH-Package</i> with name {@value GH_PACkAGE_NAME}, that is hardcoded here,
     * but it could be obtained via {@code gh api} or using <i>GH-workflow</i> {@code misc--gh-packages.yml}.
     */
    public static final String GH_PACkAGE_ID = "2631343";

    /**
     * URL to the root page of <i>GH-Package</i> with ID {@value GH_PACkAGE_ID}, that is hardcoded here,
     * but it could be obtained via {@code gh api} or using <i>GH-workflow</i> {@code misc--gh-packages.yml}.
     */
    public static final String GH_PACkAGE_HTML_URL = "https://github.com/krm-demo/core-utils/packages/" + GH_PACkAGE_ID;

    /**
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link ArtifactoryHelper} for unit-tests
     */
    public static ArtifactoryHelper fromCtxLazy(ThymeleafToolCtx ttCtx) {
        ArtifactoryHelper helper = fromCtx(ttCtx);
        if (helper == null) {
            ArtifactoryHelper.register(ttCtx);
            GithubBadgeHelper.register(ttCtx);
            GithubHelper.register(ttCtx);
            MavenHelper.register(ttCtx);
            helper = fromCtx(ttCtx);
        }
        return helper;
    }

    /**
     * A factory-method that returns an instance of {@link ArtifactoryHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link ArtifactoryHelper} for access from other helpers
     */
    public static ArtifactoryHelper fromCtx(ThymeleafToolCtx ttCtx) {
        return ttCtx.typedVar(VAR_NAME__HELPER, ArtifactoryHelper.class);
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependencies between <b>{@code th-tool}</b> and helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        ArtifactoryHelper helper = new ArtifactoryHelper(ttCtx);
        ttCtx.setVariable(VAR_NAME__HELPER, helper);
    }

    private final ThymeleafToolCtx ttCtx;
    private ArtifactoryHelper(ThymeleafToolCtx ttCtx) {
        this.ttCtx = Objects.requireNonNull(ttCtx);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * An instance of {@link BadgeVersionProvider} that provides the short badges to GH-Packages for concrete version.
     * <hr/>
     * HTML-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgShort.of(minorGroup).badgeHtml })]
     *     [(${ ah.ghPkgShort.badgeHtml(minorGroup) })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgShort.of(minorGroup).badgeMD })]
     *     [(${ ah.ghPkgShort.badgeMD(minorGroup) })]
     * }
     */
    @Getter
    private final BadgeVersionProvider ghPkgShort = new BadgeVersionProvider.FuncRec(
        this::badgeGHPkgShortUrl,
        this::ghPkgUrl,
        (String _versionStr) -> "GitHub-Packages short",
        (String versionStr) -> String.format("GH-Package '%s'%s",
            GH_PACkAGE_NAME,
            isBlank(versionStr) ? "" : ":" + versionStr
        )
    );

    /**
     * An instance of {@link BadgeVersionProvider} that provides the short badges to GH-Packages for concrete version
     * <hr/>
     * HTML-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgLong.of(minorGroup).badgeHtml })]
     *     [(${ ah.ghPkgLong.badgeHtml(minorGroup) })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgLong.of(minorGroup).badgeMD })]
     *     [(${ ah.ghPkgLong.badgeMD(minorGroup) })]
     * }
     */
    @Getter
    private final BadgeVersionProvider ghPkgLong = new BadgeVersionProvider.FuncRec(
        this::badgeGHPkgLongUrl,
        this::ghPkgUrl,
        (String _versionStr) -> "GitHub-Packages long",
        (String versionStr) -> String.format("GH-Package '%s'%s",
            GH_PACkAGE_NAME,
            isBlank(versionStr) ? "" : ":" + versionStr
        )
    );

    /**
     * An instance of {@link BadgeVersionProvider} that provides the long badges to the root of GH-Packages
     * <hr/>
     * HTML-badges are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgRootShort.badgeHtml })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgRootShort.badgeMD })]
     * }
     */
    @Getter
    private final BadgeProvider ghPkgRootShort = ghPkgShort.of("");

    /**
     * An instance of {@link BadgeVersionProvider} that provides the long badges to the root of GH-Packages
     * <hr/>
     * HTML-badges are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgRootLong.badgeHtml })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgRootLong.badgeMD })]
     * }
     */
    @Getter
    private final BadgeProvider ghPkgRootLong = ghPkgLong.of("");

    // --------------------------------------------------------------------------------------------

    /**
     * This conditional badge-provider performs rendering of badges to GH-Packages
     * only when the current version of project is INTERNAL-release.
     * In other cases an empty-stub is returned as {@link BadgeProvider#EMPTY}.
     * <hr/>
     * 'GitHib Markdown'-badge at {@code README.md.th}-template is inserted as: {@snippet :
     *     [(${ ah.currentGHPkg.badgeMD })]
     * }
     *
     * @return a badge-provider to the current INTERNAL-release or {@link BadgeProvider#EMPTY an empty-stub}
     */
    public BadgeProvider getCurrentGHPkg() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        if (mh.versionHasQualifierPart()) {
            return BadgeProvider.EMPTY;
        } else if (mh.versionHasIncrementalPart()) {
            return ghPkgShort.of(mh.getCurrentProjectVersion());
        } else {
            return BadgeProvider.EMPTY;
        }
    }

    /**
     * @return URL to GH-Packages for the whole project (a root-page)
     */
    public String getGHPkgUrl() {
        return ghPkgUrl(null);
    }

    /**
     * @return URL to GH-Packages for concrete version
     */
    public String ghPkgUrl(String versionStr) {
        return GH_PACkAGE_HTML_URL + (isBlank(versionStr) ? "" : "?version=" + versionStr);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     */
    public String getBadgeGHPkgLongUrl() {
        return badgeGHPkgLongUrl("");
    }

    /**
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     */
    public String getBadgeGHPkgShortUrl() {
        return badgeGHPkgShortUrl("");
    }

    /**
     * @param versionStr string representation of GH-Package's version
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    public String badgeGHPkgLongUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO(GH_PACkAGE_NAME, versionStr,
            isBlank(versionStr) ? "b0e0e6" : "blue",
            LOGO_SLUG_NAME__GIT_HUB,
            "black",
            "b0e0e6" // <-- this color is called "PowderBlue" at https://htmlcolorcodes.com/color-names/
        );
    }

    /**
     * @param versionStr string representation of GH-Package's version
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    public String badgeGHPkgShortUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO("GH-Packages", versionStr,
            isBlank(versionStr) ? "b0e0e6" : "blue",
            LOGO_SLUG_NAME__GIT_HUB,
            "black",
            "b0e0e6" // <-- this color is called "PowderBlue" at https://htmlcolorcodes.com/color-names/
        );
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
