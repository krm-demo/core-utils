package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * <br/>TODO: to be implemented !!!
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
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     * {@snippet : [(${ ah.badgeGHPkgLongHtml })] }
     * <hr/>
     * Such badge is present at main Test-Site only (and maybe in future at 'Release Catalog')
     *
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     */
    @JsonIgnore
    public String getBadgeGHPkgLongHtml() {
        return badgeGHPkgLongHtml("");
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     * {@snippet : [(${ ah.badgeGHPkgShortHtml })] }
     * <hr/>
     * Such badge is present at main Test-Site only (and maybe in future at 'Release Catalog')
     *
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     */
    @JsonIgnore
    public String getBadgeGHPkgShortHtml() {
        return badgeGHPkgShortHtml("");
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the long 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME}:
     * {@snippet : [(${ ah.badgeGHPkgLongMD })] }
     * <hr/>
     * Such badge is present at workflow-summary 'gh-packages' only (and maybe in future at 'README.md')
     *
     * @return long 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME}
     */
    public String getBadgeGHPkgLongMD() {
        return badgeGHPkgLongMD("");
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the short 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME}:
     * {@snippet : [(${ ah.badgeGHPkgShortMD })] }
     * <hr/>
     * Such badge is present at workflow-summary 'gh-packages' only (and maybe in future at 'README.md')
     *
     * @return short 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME}
     */
    public String getBadgeGHPkgShortMD() {
        return badgeGHPkgShortMD("");
    }

    // --------------------------------------------------------------------------------------------

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
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     * {@snippet : [(${ ah.badgeGHPkgLongHtml(versionStr) })] }
     * <hr/>
     * Such badge is present at main Test-Site only (and maybe in future at 'Release Catalog')
     *
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    @JsonIgnore
    public String badgeGHPkgLongHtml(String versionStr) {
        return String.format("""
            <a href="%s">
              <img alt="a long badge to GH-Package" src="%s" />
            </a>""",
            ghPkgUrl(versionStr),
            badgeGHPkgLongUrl(versionStr));
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     * {@snippet : [(${ ah.badgeGHPkgShortHtml(versionStr) })] }
     * <hr/>
     * Such badge is present at main Test-Site only (and maybe in future at 'Release Catalog')
     *
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    @JsonIgnore
    public String badgeGHPkgShortHtml(String versionStr) {
        return String.format("""
            <a href="%s" title="GH-Package '%s'%s">
              <img alt="a short badge to GH-Package" src="%s" />
            </a>""",
            ghPkgUrl(versionStr),
            GH_PACkAGE_NAME,
            isBlank(versionStr) ? "" : ":" + versionStr,
            badgeGHPkgShortUrl(versionStr));
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the long 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version:
     * {@snippet : [(${ ah.badgeGHPkgLongMD(versionStr) })] }
     * <hr/>
     * Such badge is present at workflow-summary 'gh-packages' only (and maybe in future at 'README.md')
     *
     * @return long 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    public String badgeGHPkgLongMD(String versionStr) {
        return String.format(
            "[![GitHub-Packages long](%s)](%s)",
            badgeGHPkgLongUrl(versionStr),
            ghPkgUrl(versionStr));
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the short 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version:
     * {@snippet : [(${ ah.badgeGHPkgShortMD(versionStr) })] }
     * <hr/>
     * Such badge is present at workflow-summary 'gh-packages' only (and maybe in future at 'README.md')
     *
     * @return short 'GitHub-Markdown'-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     */
    public String badgeGHPkgShortMD(String versionStr) {
        return String.format(
            "[![GitHub-Packages short](%s)](%s \"GH-Package '%s'%s\")",
            badgeGHPkgShortUrl(versionStr),
            ghPkgUrl(versionStr),
            GH_PACkAGE_NAME,
            isBlank(versionStr) ? "" : ":" + versionStr
        );
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param commitGroupMinor minor commit-group to get the GH-Package's version
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMinor}
     */
    public String badgeGHPkgLongHtml(CommitGroupMinor commitGroupMinor) {
        return badgeGHPkgLongHtml("" + commitGroupMinor.versionTag());
    }

    /**
     * A helper-property to be inserted at <b>{@code th-tool}</b>-template in order to render
     * the short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME} for concrete version
     * {@snippet : [(${ ah.badgeGHPkgShortHtml(minorGroup) })] }
     * <hr/>
     * Such badge is present at 'Release Catalog'
     *
     * @param minorGroup minor commit-group to get the GH-Package's version
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code minorGroup}
     */
    public String badgeGHPkgShortHtml(CommitGroupMinor minorGroup) {
        return badgeGHPkgShortHtml("" + minorGroup.versionTag());
    }

    /**
     * TODO: must return the badge to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgLongHtml(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgLongHtml("" + commitGroupMajor.versionTag());
    }

    /**
     * TODO: must return the badge to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgShortHtml(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgShortHtml("" + commitGroupMajor.versionTag());
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param commitGroupMinor minor commit-group to get the GH-Package's version
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMinor}
     */
    public String badgeGHPkgLongMD(CommitGroupMinor commitGroupMinor) {
        return badgeGHPkgLongMD("" + commitGroupMinor.versionTag());
    }

    /**
     * @param commitGroupMinor minor commit-group to get the GH-Package's version
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMinor}
     */
    public String badgeGHPkgShortMD(CommitGroupMinor commitGroupMinor) {
        return badgeGHPkgShortMD("" + commitGroupMinor.versionTag());
    }

    /**
     * TODO: must return the badge to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return long HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgLongMD(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgLongMD("" + commitGroupMajor.versionTag());
    }

    /**
     * TODO: must return the badge to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return short HTML-badge to the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgShortMD(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgShortMD("" + commitGroupMajor.versionTag());
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

    /**
     * @param commitGroupMinor minor commit-group to get the GH-Package's version
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMinor}
     */
    public String badgeGHPkgLongUrl(CommitGroupMinor commitGroupMinor) {
        return badgeGHPkgLongUrl("" + commitGroupMinor.versionTag());
    }

    /**
     * @param commitGroupMinor minor commit-group to get the GH-Package's version
     * @return the URL to the short badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMinor}
     */
    public String badgeGHPkgShortUrl(CommitGroupMinor commitGroupMinor) {
        return badgeGHPkgShortUrl("" + commitGroupMinor.versionTag());
    }

    /**
     * TODO: must return the URL to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return the URL to the long badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgLongUrl(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgLongUrl("" + commitGroupMajor.versionTag());
    }

    /**
     * TODO: must return the URL to "Maven-Central", but not to "GH-Package"
     *
     * @param commitGroupMajor major commit-group to get the GH-Package's version
     * @return the URL to the short badge-image for the GH-Package {@value GH_PACkAGE_NAME}
     *         for concrete version, represented by passed {@code commitGroupMajor}
     */
    public String badgeGHPkgShortUrl(CommitGroupMajor commitGroupMajor) {
        return badgeGHPkgShortUrl("" + commitGroupMajor.versionTag());
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
