package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.buildinfo.BuildInfo;
import org.krmdemo.techlabs.core.buildinfo.CoreUtilsBuildInfo;
import org.krmdemo.techlabs.core.buildinfo.MavenBuildInfo;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to work with maven-properties,
 * which is available from <b>{@code th-tool}</b>-templates by name {@code mh}.
 * <hr/>
 * It encapsulates the logic of incrementing the version of maven-project
 * during internal and public releases, which is usually performed by
 * <a href="https://maven.apache.org/maven-release/maven-release-plugin/">Maven Release Plugin</a>
 * with its goals:<ul>
 *     <li><a href="https://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html">
 *         release:prepare
 *     </a></li>
 *     <li><a href="https://maven.apache.org/maven-release/maven-release-plugin/prepare-mojo.html">
 *         release:prepare-with-pom
 *     </a></li>
 * </ul>
 */
@JsonPropertyOrder(alphabetic = true)
public class MavenHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link MavenHelper}
     */
    public static final String VAR_NAME__HELPER = "mh";

    /**
     * A factory-method that returns an instance of {@link MavenHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link MavenHelper} for access from other helpers
     */
    public static MavenHelper fromCtx(ThymeleafToolCtx ttCtx) {
        MavenHelper helper = ttCtx.typedVar(VAR_NAME__HELPER, MavenHelper.class);
        if (helper == null) {
            register(ttCtx);
            helper = ttCtx.typedVar(VAR_NAME__HELPER, MavenHelper.class);
        }
        return helper;
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependencies between <b>{@code th-tool}</b> and helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        ttCtx.setVariable(VAR_NAME__HELPER, new MavenHelper());
    }

    @Getter
    @JsonIgnore
    private final BuildInfo buildInfo;

    /**
     * Constructor over the build-information of the current maven-project
     */
    public MavenHelper() {
        this(CoreUtilsBuildInfo.getInstance());
    }

    /**
     * @param buildInfo build-information as {@link BuildInfo}
     */
    public MavenHelper(BuildInfo buildInfo) {
        this.buildInfo = Objects.requireNonNull(buildInfo);
    }

    /**
     * Constructor over the test-resource that simulates the real build-information.
     * It implies that test-classpath contains the properties-file at location:<pre>{@code
     *  /META-INF/maven/'test-project-group'/'test-project-artifact'/maven-project.properties
     *  }</pre>
     *  ... where <ul>
     *      <li>{@code 'test-project-group'} - the value of parameter {@code testProjectGroup}</li>
     *      <li>{@code 'test-project-artifact'} - the value of parameter {@code testProjectArtifact}</li>
     *  </ul>
     *
     * @param testProjectGroup test maven-project group
     * @param testProjectArtifact test maven-project artifact
     */
    public MavenHelper(String testProjectGroup, String testProjectArtifact) {
        this(new MavenBuildInfo(testProjectGroup, testProjectArtifact));
    }

    // --------------------------------------------------------------------------------------------

    private final static String PROP_NAME__MAJOR_VERSION = "parsedVersion.majorVersion";
    private final static String PROP_NAME__MINOR_VERSION = "parsedVersion.minorVersion";
    private final static String PROP_NAME__INCREMENTAL_VERSION = "parsedVersion.incrementalVersion";
    private final static String PROP_NAME__VERSION_QUALIFIER = "parsedVersion.qualifier";

    public String getResourcePath() {
        return buildInfo.getResourcePath();
    }

    /**
     * @return colon({@code ':'})-separated {@link #getProjectArtifact()} and {@link #getCalculatedProjectVersion()}
     */
    public String getProjectName() {
        return buildInfo.getProjectArtifact() + ":" + buildInfo.getProjectVersion();
    }

    /**
     * @return the same as {@link #getProjectName()}, but colon({@code ":"})-symbol is substituted with dash({@code '-'}
     */
    public String getProjectCatalogName() {
        return getProjectName().replace(':', '-');
    }

    /**
     * @return the name of the project group ({@code "io.github.krm-demo"})
     */
    public String getProjectGroup() {
        return buildInfo.getProjectGroup();
    }

    /**
     * @return the name of the project artifact ({@code "core-utils"})
     */
    public String getProjectArtifact() {
        return buildInfo.getProjectArtifact();
    }

    /**
     * @return the full current version of project in {@code pom.xml} (like {@code 21.04.008-SNAPSHOT})
     */
    public String getCurrentProjectVersion() {
        return buildInfo.getProjectVersion();
    }

    /**
     * @return other properties of build-information as {@link NavigableMap NavigableMap&lt;String,String&gt;}
     */
    public NavigableMap<String, String> getProps() {
        return buildInfo.getProjectProps();
    }

    /**
     * For the project version like {@code 21.04.008-SNAPSHOT} this method returns like {@code 21.04.008}
     *
     * @return the same as {@link #getCurrentProjectVersion()}, but without qualifier
     */
    public String getInternalReleaseVersion() {
        return String.format(INTERNAL_RELEASE_VERSION_FORMAT,
            getMajorVersionAsInt(),
            getMinorVersionAsInt(),
            getIncrementalAsInt()
        );
    }

    /**
     * For the project version like {@code 21.04.008-SNAPSHOT} this method returns like {@code 21.04}
     *
     * @return the same as {@link #getCurrentProjectVersion()}, but without qualifier and incremental version
     */
    public String getPublicReleaseVersion() {
        return String.format(PUBLIC_RELEASE_VERSION_FORMAT,
            getMajorVersionAsInt(),
            getMinorVersionAsInt()
        );
    }

    /**
     * For the project version like {@code 21.04.008-SNAPSHOT} this method returns like {@code 21.05.001-SNAPSHOT}
     *
     * @return calculated from {@link #getCurrentProjectVersion()},
     * by increasing the minor version and reset the incremental version to {@code 1}
     */
    public String getPublicNextVersion() {
        return String.format(SNAPSHOT_VERSION_FORMAT,
            getMajorVersionAsInt(),
            getMinorVersionAsInt() + 1,
            1,
            getVersionQualifier()
        );
    }

    /**
     * For the project version like {@code 21.04.008-SNAPSHOT} this method returns like {@code 21.05.009-SNAPSHOT}
     *
     * @return calculated from {@link #getCurrentProjectVersion()}, by increasing the incremental version
     */
    public String getInternalNextVersion() {
        return String.format(SNAPSHOT_VERSION_FORMAT,
            getMajorVersionAsInt(),
            getMinorVersionAsInt(),
            getIncrementalAsInt() + 1,
            getVersionQualifier()
        );
    }

    /**
     * This property is helpful, when for some reason the value of version in {@code pom.xml} is corrupted.
     *
     * @return in normal scenario exactly the same as {@link #getCurrentProjectVersion}
     */
    public String getCalculatedProjectVersion() {
        return String.format(SNAPSHOT_VERSION_FORMAT,
            getMajorVersionAsInt(),
            getMinorVersionAsInt(),
            getIncrementalAsInt(),
            getVersionQualifier()
        );
    }

    /**
     * @return in this project this method must always return the value {@code "21"},
     * which corresponds to the minimal version of JDK this library is built with
     */
    public String getMajorVersion() {
        return buildInfo.getProjectProps().getOrDefault(PROP_NAME__MAJOR_VERSION, "21");
    }

    /**
     * @return the value of {@link #getMajorVersion()} as {@link Integer}
     */
    public Integer getMajorVersionAsInt() {
        return Integer.valueOf(getMajorVersion());
    }

    /**
     * @return the minor version corresponds to ordinal number of <b>public release</b>
     */
    public String getMinorVersion() {
        return buildInfo.getProjectProps().getOrDefault(PROP_NAME__MINOR_VERSION, "0");
    }

    /**
     * @return the value of {@link #getMinorVersion()} as {@link Integer}
     */
    public Integer getMinorVersionAsInt() {
        return Integer.valueOf(getMinorVersion());
    }

    /**
     * @return the minor version corresponds to the ordinal number of <b>internal release</b>
     * since the last <b>public release</b> (should be {@code 0} right after the public release is performed)
     */
    public String getIncrementalVersion() {
        return buildInfo.getProjectProps().getOrDefault(PROP_NAME__INCREMENTAL_VERSION, "0");
    }

    /**
     * @return the value of {@link #getIncrementalVersion()} as {@link Integer}
     */
    public int getIncrementalAsInt() {
        try {
            return Integer.parseInt(getIncrementalVersion());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * @return in this project this method must always return the value {@code "SNAPSHOT"}
     */
    public String getVersionQualifier() {
        String qualifier = buildInfo.getProjectProps().get(PROP_NAME__VERSION_QUALIFIER);
        return StringUtils.isBlank(qualifier) ? "SNAPSHOT" : qualifier;
    }

    @JsonProperty("versionHasQualifierPart")
    public boolean versionHasQualifierPart() {
        return StringUtils.isNotBlank(buildInfo.getProjectProps().get(PROP_NAME__VERSION_QUALIFIER));
    }

    @JsonProperty("versionHasIncrementalPart")
    public boolean versionHasIncrementalPart() {
        return getIncrementalAsInt() > 0;
    }

    public String getUsageFragmentPath() {
        return String.format(".github/th-templates/Usage-%s.md.th", getUsageFragmentSuffix());
    }

    public String getUsageFragmentSuffix() {
        if (versionHasQualifierPart()) {
            return "SNAPSHOT";
        } else if (versionHasIncrementalPart()) {
            return "INTERNAL";
        } else {
            return "PUBLIC";
        }
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * The format-string of maven-project version that is in development right now:
     * <br/>{@literal <major-version>.<minor-version>.<incremental-version>-<qualifier>}
     */
    final static String SNAPSHOT_VERSION_FORMAT = "%d.%02d.%03d-%s";

    /**
     * The same as {@link #SNAPSHOT_VERSION_FORMAT}, but without qualifier-suffix.
     */
    final static String INTERNAL_RELEASE_VERSION_FORMAT = "%d.%02d.%03d";

    /**
     * The same as {@link #INTERNAL_RELEASE_VERSION_FORMAT}, but without incremental version.
     */
    final static String PUBLIC_RELEASE_VERSION_FORMAT = "%d.%02d";

}
