package org.krmdemo.techlabs.core.buildinfo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;

/**
 * This interface represents the build-information that is usually collected by build-tool
 * into some dedicated text-resource, which are available in classpath at runtime.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "maven-coordinates",
    "resource-path",
    "build-date-time",
    "project-group",
    "project-artifact",
    "project-version",
    "minimal-jdk-version",
    "loading-errors",
})
public interface BuildInfo {

    /**
     * The path to resource that is used to represent the build-information
     *
     * @return either resource-path or any other reference to realize which resource was used for build-info
     */
    @JsonGetter("resource-path")
    String getResourcePath();

    /**
     * Getting a sorted map of build-information properties (not dumped by default).
     *
     * @return build-information properties as {@link NavigableMap NavigableMap&lt;String,String&gt;}
     */
    @JsonIgnore
    NavigableMap<String, String> getProjectProps();

    /**
     * Getting the value of maven-project group ({@code <groupId>}-tag in {@code pom.xml}),
     * which must also be a part of {@link #getResourcePath() resource-path}.
     *
     * @return the value of maven-project group ({@code <groupId>}-tag in {@code pom.xml})
     */
    @JsonGetter("project-group")
    String getProjectGroup();

    /**
     * Getting the value of maven-project artifact ({@code <artifactId>}-tag in {@code pom.xml}),
     * which must also be a part of {@link #getResourcePath() resource-path}.
     *
     * @return the value of maven-project group ({@code <artifactId>}-tag in {@code pom.xml})
     */
    @JsonGetter("project-artifact")
    String getProjectArtifact();

    /**
     * Getting the value of maven-project version ({@code <version>}-tag in {@code pom.xml}),
     * which must also be a value of corresponding <b>{@code git}</b>-tag in {@code git}-repository.
     *
     * @return the value of maven-project group ({@code <artifactId>}-tag in {@code pom.xml})
     */
    @JsonGetter("project-version")
    String getProjectVersion();

    /**
     * Getting the maven-coordinates - the colon({@code ':'})-separated {@link #getProjectGroup() project-group},
     * {@link #getProjectArtifact() project-artifact} and {@link #getProjectVersion() project-version},
     * which is also a <a href="https://gradle.org/">Gradle</a>-style identifier of any project and library.
     *
     * @return colon({@code ':'})-separated {@link #getProjectGroup() project-group},
     *         {@link #getProjectArtifact() project-artifact} and {@link #getProjectVersion() project-version}
     */
    @JsonGetter("maven-coordinates")
    default String getMavenCoordinates() {
        return getProjectGroup() + ":" + getProjectArtifact() + ":" + getProjectVersion();
    }

    /**
     * Getting the date-time when the project was built as {@link DateTimeTriplet}.
     *
     * @return the data-time, when the build was made, as {@link DateTimeTriplet}
     */
    @JsonIgnore
    DateTimeTriplet getBuildDateTime();

    /**
     * Getting the build date-time without information about day of week,
     * because in some cases the information about the day of week is not desired.
     *
     * @return formatted build date-time without information about day of week
     */
    @JsonGetter("build-date-time")
    default String getBuildDateTimeStr() {
        return getBuildDateTime() == null ? null : getBuildDateTime().dumpNoWeek();
    }

    /**
     * Getting the minimal supported version of JDK, where this library could be used
     *
     * @return minimal version of JDK, where this library could be used
     */
    @JsonGetter("minimal-java-version")
    String getMinimalJavaVersion();

    /**
     * Getting the list of loading-errors that was happened on constructing the instance of this object.
     * In normal scenario such list <u>must be empty!</u>
     *
     * @return if not empty - the list of errors that were discovered when loading the resource-content
     */
    @JsonGetter("loading-errors")
    default List<String> getLoadingErrors() {
        return Collections.emptyList();
    }
}
