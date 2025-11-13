package org.krmdemo.techlabs.core.buildinfo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;

import java.util.Collections;
import java.util.List;
import java.util.NavigableMap;

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

    @JsonGetter("resource-path")
    String getResourcePath();

    @JsonIgnore
    NavigableMap<String, String> getProjectProps();

    @JsonGetter("project-group")
    String getProjectGroup();

    @JsonGetter("project-artifact")
    String getProjectArtifact();

    @JsonGetter("project-version")
    String getProjectVersion();

    @JsonGetter("maven-coordinates")
    default String getMavenCoordinates() {
        return getProjectGroup() + ":" + getProjectArtifact() + ":" + getProjectVersion();
    }

    /**
     * @return data-time, when the build was made, as {@link DateTimeTriplet}
     */
    @JsonIgnore
    DateTimeTriplet getBuildDateTime();

    @JsonGetter("build-date-time")
    default String getBuildDateTimeStr() {
        return getBuildDateTime() == null ? null : getBuildDateTime().dumpNoWeek();
    }

    /**
     * @return minimal version of JDK, when this library is in use
     */
    @JsonGetter("minimal-java-version")
    String getMinimalJavaVersion();

    /**
     * @return if not empty - the list of errors that were discovered when loading the resource-content
     */
    @JsonGetter("loading-errors")
    default List<String> getLoadingErrors() {
        return Collections.emptyList();
    }
}
