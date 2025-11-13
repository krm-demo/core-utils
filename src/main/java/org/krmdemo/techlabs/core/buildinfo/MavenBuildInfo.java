package org.krmdemo.techlabs.core.buildinfo;

import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.utils.PropertiesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.SequencedMap;

/**
 * Implementation of {@link BuildInfo} with information that is loaded from properties-file,
 * that is expected to be at classpath as a resource with path: <pre>{@code
 * /META-INF/maven/'project-group'/'project-artifact'/maven-project.properties
 * }</pre>
 * ... where <ul>
 *     <li>{@code 'project-group'} - maven-project group</li>
 *     <li>{@code 'project-artifact'} - maven-project artifact</li>
 * </ul>
 */
public class MavenBuildInfo implements BuildInfo {

    /**
     * Property-name that holds the value of maven-project group  ({@code <groupId>}-tag in {@code pom.xml})
     */
    private final static String PROP_NAME__PROJECT_GROUP = "maven-project.group";

    /**
     * Property-name that holds the value of maven-project group  ({@code <artifactId>}-tag in {@code pom.xml})
     */
    private final static String PROP_NAME__PROJECT_ARTIFACT = "maven-project.artifact";

    /**
     * Property-name that holds the value of maven-project group  ({@code <version>}-tag in {@code pom.xml})
     */
    private final static String PROP_NAME__PROJECT_VERSION = "maven-project.version";

    /**
     * Property-name that represents the minimal JDK-version to use this library
     */
    private final static String PROP_NAME__MINIMAL_JAVA_VERSION = "minimal.java.version";


    /**
     * Property-name that represents the date-time when the project of this library was built.
     * <hr/>
     * The format of string-value corresponds to {@link java.time.format.DateTimeFormatter#ISO_INSTANT ISO_INSTANT},
     * which is also available in build-property {@code "maven.build.timestamp.format"}.
     */
    private final static String PROP_NAME__PROJECT_BUILD_DATE_TIME = "maven-project.build-date-time";

    private final String projectGroup;
    private final String projectArtifact;
    private final NavigableMap<String, String> mvnPropsMap;
    private final String resourcePath;

    private final DateTimeTriplet buildDateTime;
    private final List<String> loadingErrors = new ArrayList<>();

    /**
     * Loading the build-information from properties-file, whose resource-path
     * depends on passed {@code mavenProjectGroup} and {@code mavenProjectArtifact}
     *
     * @param mavenProjectGroup maven-project group ({@code <groupId>}-tag in {@code pom.xml} of maven-project)
     * @param mavenProjectArtifact maven-project artifact ({@code <artifactId>}-tag in {@code pom.xml} of maven-project)
     */
    public MavenBuildInfo(String mavenProjectGroup, String mavenProjectArtifact) {
        if (StringUtils.isBlank(mavenProjectGroup)) {
            throw new IllegalArgumentException("'mavenProjectGroup' must not be blank");
        }
        if (StringUtils.isBlank(mavenProjectArtifact)) {
            throw new IllegalArgumentException("'mavenProjectArtifact' must not be blank");
        }
        this.projectGroup = mavenProjectGroup.trim();
        this.projectArtifact = mavenProjectArtifact.trim();
        this.resourcePath = String.format(
            "/META-INF/maven/%s/%s/maven-project.properties",
            mavenProjectGroup, mavenProjectArtifact
        );
        this.mvnPropsMap = PropertiesUtils.propsMapResource(resourcePath);
        this.buildDateTime = parseBuildDateTime();
        checkForErrors();

    }

    private DateTimeTriplet parseBuildDateTime() {
        if (mvnPropsMap.containsKey(PROP_NAME__PROJECT_BUILD_DATE_TIME)) {
            errorMsg(String.format("no property '%s' to get the build date-time value",
                PROP_NAME__PROJECT_BUILD_DATE_TIME));
            return null;
        }
        try {
            return CoreDateTimeUtils.dtt(mvnPropsMap.get(PROP_NAME__PROJECT_BUILD_DATE_TIME));
        } catch (Exception ex) {
            errorMsg(String.format("could not parse the property '%s' to get the build date-time value - %s",
                PROP_NAME__PROJECT_BUILD_DATE_TIME, ex.getMessage()));
            return null;
        }
    }

    private void checkForErrors() {
        if (this.mvnPropsMap.isEmpty()) {
            errorMsg(String.format(
                "loaded props-map is empty - most probably the resource '%s' is not available",
                this.resourcePath
            ));
            return;
        }
        String projectGroupValue = this.mvnPropsMap.get(PROP_NAME__PROJECT_GROUP);
        if (!this.projectGroup.equals(projectGroupValue)) {
            errorMsg(String.format(
                "value of maven-project group '%s' in resource '%s' does not equal to expected value '%s'",
                this.projectGroup, this.resourcePath, projectGroupValue
            ));
        }
        String projectArtifactValue = this.mvnPropsMap.get(PROP_NAME__PROJECT_ARTIFACT);
        if (!this.projectArtifact.equals(projectArtifactValue)) {
            errorMsg(String.format(
                "value of maven-project artifact '%s' in resource '%s' does not equal to expected value '%s'",
                this.projectArtifact, this.resourcePath, projectArtifactValue
            ));
        }
        if (StringUtils.isBlank(this.getProjectVersion())) {
            errorMsg(String.format(
                "value of maven-project version (property '%s') is blank - most probably the resource '%s' is corrupted",
                PROP_NAME__PROJECT_VERSION, this.resourcePath
            ));
        }
    }

    private void errorMsg(String errorMsg) {
        this.loadingErrors.add(errorMsg);
    }

    @Override
    public String getProjectGroup() {
        return this.projectGroup;
    }

    @Override
    public String getProjectArtifact() {
        return this.projectArtifact;
    }

    @Override
    public NavigableMap<String, String> getProjectProps() {
        return Collections.unmodifiableNavigableMap(mvnPropsMap);
    }

    @Override
    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public String getProjectVersion() {
        return this.mvnPropsMap.get(PROP_NAME__PROJECT_VERSION);
    }

    @Override
    public DateTimeTriplet getBuildDateTime() {
        return this.buildDateTime;
    }

    @Override
    public String getMinimalJavaVersion() {
        return this.mvnPropsMap.get(PROP_NAME__MINIMAL_JAVA_VERSION);
    }

    @Override
    public List<String> getLoadingErrors() {
        return Collections.unmodifiableList(this.loadingErrors);
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsYamlTxt(this);
    }
}
