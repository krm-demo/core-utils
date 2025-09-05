package org.krmdemo.techlabs.classinfo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.sysdump.PropertiesUtils;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.TechlabsCollectors.toSortedMap;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record JarFileInfo(
    @JsonIgnore JarFile jarFile
) {
    public JarFileInfo {
        Objects.requireNonNull(jarFile);
    }

    Optional<Manifest> manifest() {
        try {
            return Optional.ofNullable(jarFile.getManifest());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @JsonGetter
    NavigableMap<String, String> getManifestMain() {
        return sortedMap(manifest().stream()
            .map(Manifest::getMainAttributes)
            .flatMap(JarFileInfo::manifestAttrs));
    }

    @JsonGetter
    NavigableMap<String, NavigableMap<String, String>> getManifestSections() {
        return manifest().stream()
            .map(Manifest::getEntries)
            .flatMap(JarFileInfo::manifestSections)
            .collect(toSortedMap(JarFileInfo::attrsEntryMap));
    }

    private static NavigableMap<String, String> attrsEntryMap(Map.Entry<?,Attributes> attrsEntry) {
        return sortedMap(manifestAttrs(attrsEntry.getValue()));
    }

    private static Stream<Map.Entry<String, String>> manifestAttrs(Attributes attrs) {
        return attrs.entrySet().stream().map(PropertiesUtils::propEntry);
    }

    private static Stream<Map.Entry<String, Attributes>> manifestSections(Map<String, Attributes> sections) {
        return sections.entrySet().stream();
    }
}
