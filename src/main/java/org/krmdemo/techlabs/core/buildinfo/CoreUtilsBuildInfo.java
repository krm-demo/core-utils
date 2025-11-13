package org.krmdemo.techlabs.core.buildinfo;

import lombok.Getter;

/**
 * This Java-singleton represents the build-information of {@value #MAVEN_PROJECT_ARTIFACT}-library,
 * which is loaded from {@code maven-project.properties} resource via parent class {@link MavenBuildInfo}.
 */
public class CoreUtilsBuildInfo extends MavenBuildInfo {

    /**
     * the value of {@code <groupId>}-tag in {@code pom.xml} of the current maven-project
     */
    public static final String MAVEN_PROJECT_GROUP = "io.github.krm-demo";

    /**
     * the value of {@code <artifactId>}-tag in {@code pom.xml} of the current maven-project
     */
    public static final String MAVEN_PROJECT_ARTIFACT = "core-utils";

    /**
     * a single instance of {@link CoreUtilsBuildInfo}
     */
    @Getter
    private static final CoreUtilsBuildInfo instance = new CoreUtilsBuildInfo();

    private CoreUtilsBuildInfo() {
        super(MAVEN_PROJECT_GROUP, MAVEN_PROJECT_ARTIFACT);
    }
}
