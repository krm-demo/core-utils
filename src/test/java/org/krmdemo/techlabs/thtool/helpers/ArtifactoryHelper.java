package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;
import org.krmdemo.techlabs.thtool.badges.BadgeProvider;
import org.krmdemo.techlabs.thtool.badges.BadgeVersionProvider;

import java.nio.file.Path;
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
@JsonPropertyOrder(alphabetic = true)
public class ArtifactoryHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link ArtifactoryHelper}
     */
    public static final String VAR_NAME__HELPER = "ah";

    /**
     * The ID of <i>GH-Package</i> with name {@link #getGhPackageName()}, that is hardcoded here,
     * but it could be obtained via {@code gh api} or using <i>GH-workflow</i> {@code misc--gh-packages.yml}.
     */
    public static final String GH_PACkAGE_ID = "2631343";

    /**
     * URL to the root page of <i>GH-Package</i> with ID {@value GH_PACkAGE_ID}, that is hardcoded here,
     * but it could be obtained via {@code gh api} or using <i>GH-workflow</i> {@code misc--gh-packages.yml}.
     */
    public static final String GH_PACkAGE_HTML_URL = "https://github.com/krm-demo/core-utils/packages/" + GH_PACkAGE_ID;

    // ---------------------------------------------------------------------------------------------------------------------
    // a root URL to Maven-Central site:
    // "https://central.sonatype.com"
    //
    // an example of URL to some project at Maven-Central (below is "slf4j-api"-library, which is on of our dependencies):
    // "https://central.sonatype.com/artifact/org.slf4j/slf4j-api"
    //
    // an example for the URL above to "slf4j-api"-library at Maven-Central, but for concrete version "2.0.17":
    // "https://central.sonatype.com/artifact/org.slf4j/slf4j-api/2.0.17"
    // our one: "https://central.sonatype.com/artifact/io.github.krm-demo/core-utils/21.23"
    // ---------------------------------------------------------------------------------------------------------------------

    /**
     * URL to "Maven-Central"-site:
     */
    public static final String MAVEN_CENTRAL_HTML_URL = "https://central.sonatype.com";

    // ---------------------------------------------------------------------------------------------------------------------
    // a root URL to MVN-Repository site:
    // "https://mvnrepository.com/"
    //
    // an example of URL to some project at MVN-Repository (below is "slf4j-api"-library, which is on of our dependencies):
    // "https://mvnrepository.com/artifact/org.slf4j/slf4j-api"
    //
    // an example for the URL above to "slf4j-api"-library at MVN-Repository, but for concrete version "2.0.17":
    // "https://mvnrepository.com/artifact/org.slf4j/slf4j-api/2.0.17"
    // our one: "https://mvnrepository.com/artifact/io.github.krm-demo/core-utils/21.23"
    // ---------------------------------------------------------------------------------------------------------------------

    /**
     * URL to "MVN-Repository"-site:
     */
    public static final String MVN_REPOSITORY_HTML_URL = "https://mvnrepository.com";

    // --------------------------------------------------------------------------------------------

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
     * <hr/>
     * HTML-badge for root is inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgShort.root.badgeHtml })]
     * }
     * 'GitHib Markdown'-badge for root are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgShort.root.badgeMD })]
     * }
     */
    @Getter
    private final BadgeVersionProvider ghPkgShort = new BadgeVersionProvider.FuncRec(
        this::badgeGHPkgShortUrl,
        this::ghPkgUrl,
        (String _versionStr) -> "GitHub-Packages short",
        (String versionStr) -> String.format("GH-Package '%s'%s",
            getGhPackageName(),
            isBlank(versionStr) ? "" : ":" + versionStr
        )
    );

    /**
     * An instance of {@link BadgeVersionProvider} that provides the long badges to GH-Packages for concrete version
     * <hr/>
     * HTML-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgLong.of(minorGroup).badgeHtml })]
     *     [(${ ah.ghPkgLong.badgeHtml(minorGroup) })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.ghPkgLong.of(minorGroup).badgeMD })]
     *     [(${ ah.ghPkgLong.badgeMD(minorGroup) })]
     * }
     * <hr/>
     * HTML-badge for root is inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgLong.root.badgeHtml })]
     * }
     * 'GitHib Markdown'-badge for root are inserted via following th-inline: {@snippet :
     *     [(${ ah.ghPkgLong.root.badgeMD })]
     * }
     */
    @Getter
    private final BadgeVersionProvider ghPkgLong = new BadgeVersionProvider.FuncRec(
        this::badgeGHPkgLongUrl,
        this::ghPkgUrl,
        (String _versionStr) -> "GitHub-Packages long",
        (String versionStr) -> String.format("GH-Package '%s'%s",
            getGhPackageName(),
            isBlank(versionStr) ? "" : ":" + versionStr
        )
    );

    // --------------------------------------------------------------------------------------------

    /**
     * An instance of {@link BadgeVersionProvider} that provides the badges to Maven-Central for concrete version.
     * <hr/>
     * HTML-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.mavenCentral.of(majorGroup).badgeHtml })]
     *     [(${ ah.mavenCentral.badgeHtml(majorGroup) })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.mavenCentral.of(majorGroup).badgeMD })]
     *     [(${ ah.mavenCentral.badgeHtml(majorMD })]
     * }
     */
    @Getter
    private final BadgeVersionProvider mavenCentral = new BadgeVersionProvider.FuncRec(
        this::badgeMavenCentralUrl,
        this::mavenCentralUrl,
        (String _versionStr) -> "Maven-Central site",
        (String versionStr) -> "Maven-Central site" +
            (isBlank(versionStr) ? "" : " for '" + mavenCoordinates(versionStr) + "'")
    );

    /**
     * An instance of {@link BadgeVersionProvider} that provides the badges to MVN-Repository for concrete version.
     * <hr/>
     * HTML-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.mvnRepository.of(majorGroup).badgeHtml })]
     *     [(${ ah.mvnRepository.badgeHtml(majorGroup) })]
     * }
     * 'GitHib Markdown'-badges are inserted via following th-inlines: {@snippet :
     *     [(${ ah.mvnRepository.of(majorGroup).badgeMD })]
     *     [(${ ah.mvnRepository.badgeHtml(majorMD })]
     * }
     */
    @Getter
    private final BadgeVersionProvider mvnRepository = new BadgeVersionProvider.FuncRec(
        this::badgeMvnRepositoryUrl,
        this::mvnRepositoryUrl,
        (String _versionStr) -> "MVN-Repository site",
        (String versionStr) -> "MVN-Repository site" +
            (isBlank(versionStr) ? "" : " for '" + mavenCoordinates(versionStr) + "'")
    );

    // --------------------------------------------------------------------------------------------

    /**
     * This conditional badge-provider performs rendering the badges to GH-Packages
     * for the current INTERNAL-release (only when the current version of project is INTERNAL-release).
     * In other cases an empty-stub is returned as {@link BadgeProvider#EMPTY}.
     * <hr/>
     * 'GitHib Markdown'-badge at {@code README.md.th}-template is inserted as: {@snippet :
     *     [(${ ah.currentGHPkg.badgeMD })]
     * }
     *
     * @return a badge-provider to the GH-Packages of the current INTERNAL-release or {@link BadgeProvider#EMPTY an empty-stub}
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
     * This conditional badge-provider performs rendering the badges to Maven-Central
     * for the current PUBLIC-release (only when the current version of project is PUBLIC-release).
     * In other cases an empty-stub is returned as {@link BadgeProvider#EMPTY}.
     * <hr/>
     * 'GitHib Markdown'-badge at {@code README.md.th}-template is inserted as: {@snippet :
     *     [(${ ah.currentMavenCentral.badgeMD })]
     * }
     *
     * @return a badge-provider to Maven-Central of the current PUBLIC-release or {@link BadgeProvider#EMPTY an empty-stub}
     */
    public BadgeProvider getCurrentMavenCentral() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        if (mh.versionHasIncrementalPart()) {
            return BadgeProvider.EMPTY;
        } else {
            return mavenCentral.of(mh.getCurrentProjectVersion());
        }
    }

    /**
     * This conditional badge-provider performs rendering the badges to MVN-Repository
     * for the current PUBLIC-release (only when the current version of project is PUBLIC-release).
     * In other cases an empty-stub is returned as {@link BadgeProvider#EMPTY}.
     * <hr/>
     * 'GitHib Markdown'-badge at {@code README.md.th}-template is inserted as: {@snippet :
     *     [(${ ah.currentMvnRepository.badgeMD })]
     * }
     *
     * @return a badge-provider to MVN-Repository of the current PUBLIC-release or {@link BadgeProvider#EMPTY an empty-stub}
     */
    public BadgeProvider getCurrentMvnRepository() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        if (mh.versionHasIncrementalPart()) {
            return BadgeProvider.EMPTY;
        } else {
            return mvnRepository.of(mh.getCurrentProjectVersion());
        }
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return URL to GH-Packages for concrete version of the current project
     */
    public String ghPkgUrl(String versionStr) {
        return GH_PACkAGE_HTML_URL + (isBlank(versionStr) ? "" : "?version=" + versionStr);
    }

    /**
     * @param versionStr maven-project version
     * @return the URL to the long badge-image for the GH-Package {@link #getGhPackageName()} for concrete version
     */
    public String badgeGHPkgLongUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO(
            getGhPackageName(),
            versionStr,
            isBlank(versionStr) ? "b0e0e6" : "blue",
            LOGO_SLUG_NAME__GIT_HUB,
            "black",
            "b0e0e6" // <-- this color is called "PowderBlue" at https://htmlcolorcodes.com/color-names/
        );
    }

    /**
     * @param versionStr maven-project version
     * @return the URL to the long badge-image for the GH-Package {@link #getGhPackageName()} for concrete version
     */
    public String badgeGHPkgShortUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO(
            "GH-Packages",  // <-- this is shorter than value returning by "getGhPackageName()"
            versionStr,
            isBlank(versionStr) ? "b0e0e6" : "blue",
            LOGO_SLUG_NAME__GIT_HUB,
            "black",
            "b0e0e6" // <-- this color is called "PowderBlue" at https://htmlcolorcodes.com/color-names/
        );
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param versionStr maven-project version
     * @return URL to Maven-Central for concrete version of this project
     */
    public String mavenCentralUrl(String versionStr) {
        if (isBlank(versionStr)) {
            return MAVEN_CENTRAL_HTML_URL;
        } else {
            return MAVEN_CENTRAL_HTML_URL + "/" + getMavenSitePath() + "/" + versionStr;
        }
    }

    /**
     * @param versionStr maven-project version
     * @return URL to Maven-Central for concrete version of this project
     */
    public String mvnRepositoryUrl(String versionStr) {
        if (isBlank(versionStr)) {
            return MVN_REPOSITORY_HTML_URL;
        } else {
            return MVN_REPOSITORY_HTML_URL + "/" + getMavenSitePath() + "/" + versionStr;
        }
    }

    /**
     * @param versionStr maven-project version
     * @return the URL to the badge-image for "Maven-Central"-site for concrete project-version
     */
    public String badgeMavenCentralUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO(
            "maven-central",
            versionStr,
            isBlank(versionStr) ? "415d7c" : "blue",
            Path.of(".github/images/maven-central-logo-short.svg").toFile(),
            "415d7c",  // <-- this color is a little bit lighter than background of "maven-central" site
            "415d7c"  // <-- this color is a little bit lighter than background of "maven-central" site
        );
    }

    /**
     * @param versionStr maven-project version
     * @return the URL to the badge-image for "MVN-Repository"-site for concrete project-version
     */
    public String badgeMvnRepositoryUrl(String versionStr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtx(ttCtx);
        return gbh.badgeUrlShiedsIO(
            "mvn-repo",  // <-- maybe it would be better to use the word "mvnrepository"
            versionStr,
            isBlank(versionStr) ? "eee" : "blue",
            Path.of(".github/images/mvnrepo-1-letter.svg").toFile(),
            "eee",  // <-- this color corresponds to title-background of "MVN-Repository" site
            "eee"  // <-- this color corresponds to title-background of "MVN-Repository" site
        );
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Getting the name of GH-Package for this maven-project,
     * which is dot{@code '.'}-separated project-group and project-artifact.
     *
     * @return the name of GH-Package for this maven-project
     */
    public String getGhPackageName() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        return mh.getProjectGroup() + "." + mh.getProjectArtifact();
    }

    /**
     * Getting the path to the current project artifacts at either "Maven-Central" or "MVN-Repository sites,
     * which is slash{@code '/'}-separated project-group and project-artifact.
     *
     * @return the path to the current project artifacts at either "Maven-Central" or "MVN-Repository sites
     */
    public String getMavenSitePath() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        return "artifact/" + mh.getProjectGroup() + "/" + mh.getProjectArtifact();
    }

    /**
     * Getting the <a href="https://gradle.org/">Gradle</a>-style coordinates for the current version of maven-project.
     *
     * @return Gradle-style coordinates for the current version of maven-project
     */
    public String getMavenCoordinates() {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        return mavenCoordinates(mh.getCurrentProjectVersion());
    }

    /**
     * Getting the <a href="https://gradle.org/">Gradle</a>-style coordinates,
     * which are colon{@code ':'}-separated project-group, project-artifact and project-version
     * <hr/>
     * This is used mostly for link-tooltips, but not for real reference as gradle-dependencies
     *
     * @param versionStr maven-project version
     * @return <a href="https://gradle.org/">Gradle</a>-style identifier of any project and library.
     */
    public String mavenCoordinates(String versionStr) {
        MavenHelper mh = MavenHelper.fromCtx(ttCtx);
        return mh.getProjectGroup() + ":" + mh.getProjectArtifact() + ":" + versionStr;
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
