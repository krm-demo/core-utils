package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for {@link MavenHelper}
 */
public class MavenHelperTest {

    private final MavenHelper mavenHelper = new MavenHelper(
        "/org/krmdemo/techlabs/core/thtool/test-01--maven-project.properties");
    private final MavenHelper mavenHelperInternal = new MavenHelper(
        "/org/krmdemo/techlabs/core/thtool/test-02--maven-project.properties");
    private final MavenHelper mavenHelperPublic = new MavenHelper(
        "/org/krmdemo/techlabs/core/thtool/test-03--maven-project.properties");

    @Test
    void testResourcePath() {
        assertThat(mavenHelper.getResourcePath())
            .endsWith("test-01--maven-project.properties");
        assertThat(mavenHelperInternal.getResourcePath())
            .endsWith("test-02--maven-project.properties");
        assertThat(mavenHelperPublic.getResourcePath())
            .endsWith("test-03--maven-project.properties");
    }

    @Test
    void testUsageFragmentPath() {
        assertThat(mavenHelper.getUsageFragmentPath())
            .endsWith(".github/th-templates/Usage-SNAPSHOT.md.th");
        assertThat(mavenHelperInternal.getUsageFragmentPath())
            .endsWith(".github/th-templates/Usage-INTERNAL.md.th");
        assertThat(mavenHelperPublic.getUsageFragmentPath())
            .endsWith(".github/th-templates/Usage-PUBLIC.md.th");
    }

    @Test
    void testBadgeName() {
        assertThat(mavenHelper.getProjectBadgeName())
            .isEqualTo("core--utils:21.0.2--SNAPSHOT");
        assertThat(mavenHelperInternal.getProjectBadgeName())
            .isEqualTo("core--utils:21.0.25");
        assertThat(mavenHelperPublic.getProjectBadgeName())
            .isEqualTo("core--utils--la--la--la:21.0");
    }

    @Test
    void testCatalogName() {
        assertThat(mavenHelper.getProjectCatalogName())
            .isEqualTo("core-utils-21.0.2-SNAPSHOT");
        assertThat(mavenHelperInternal.getProjectCatalogName())
            .isEqualTo("core-utils-21.0.25");
        assertThat(mavenHelperPublic.getProjectCatalogName())
            .isEqualTo("core-utils-la-la-la-21.0");
    }

    @Test
    void testCurrentVersions() {
        assertThat(mavenHelper.getCalculatedProjectVersion())
            .startsWith(mavenHelper.getInternalReleaseVersion());
        assertThat(mavenHelper.getCalculatedProjectVersion())
            .startsWith(mavenHelper.getPublicReleaseVersion());
        assertThat(mavenHelper.getInternalReleaseVersion())
            .startsWith(mavenHelper.getPublicReleaseVersion());

        assertThat(mavenHelper.getCurrentProjectVersion())
            .isEqualTo("21.0.2-SNAPSHOT");
        assertThat(mavenHelper.getCalculatedProjectVersion())
            .isEqualTo("21.00.002-SNAPSHOT");
        assertThat(mavenHelper.getInternalReleaseVersion())
            .isEqualTo("21.00.002");
        assertThat(mavenHelper.getPublicReleaseVersion())
            .isEqualTo("21.00");
    }

    @Test
    void testNextVersions() {
        assertThat(mavenHelper.getInternalNextVersion())
            .isEqualTo("21.00.003-SNAPSHOT");
        assertThat(mavenHelper.getPublicNextVersion())
            .isEqualTo("21.01.001-SNAPSHOT");
    }

    @Test
    void testCurrentVersions_Internal() {
        assertThat(mavenHelperInternal.getCalculatedProjectVersion())
            .startsWith(mavenHelperInternal.getInternalReleaseVersion());
        assertThat(mavenHelperInternal.getCalculatedProjectVersion())
            .startsWith(mavenHelperInternal.getPublicReleaseVersion());
        assertThat(mavenHelperInternal.getInternalReleaseVersion())
            .startsWith(mavenHelperInternal.getPublicReleaseVersion());

        assertThat(mavenHelperInternal.getCurrentProjectVersion())
            .isEqualTo("21.0.25");
        assertThat(mavenHelperInternal.getCalculatedProjectVersion())
            .isEqualTo("21.00.025-SNAPSHOT");
        assertThat(mavenHelperInternal.getInternalReleaseVersion())
            .isEqualTo("21.00.025");
        assertThat(mavenHelperInternal.getPublicReleaseVersion())
            .isEqualTo("21.00");
    }

    @Test
    void testNextVersions_Internal() {
        assertThat(mavenHelperInternal.getInternalNextVersion())
            .isEqualTo("21.00.026-SNAPSHOT");
        assertThat(mavenHelperInternal.getPublicNextVersion())
            .isEqualTo("21.01.001-SNAPSHOT");
    }

    @Test
    void testCurrentVersions_Public() {
        assertThat(mavenHelperPublic.getCalculatedProjectVersion())
            .startsWith(mavenHelperPublic.getInternalReleaseVersion());
        assertThat(mavenHelperPublic.getCalculatedProjectVersion())
            .startsWith(mavenHelperPublic.getPublicReleaseVersion());
        assertThat(mavenHelperPublic.getInternalReleaseVersion())
            .startsWith(mavenHelperPublic.getPublicReleaseVersion());

        assertThat(mavenHelperPublic.getCurrentProjectVersion())
            .isEqualTo("21.0");
        assertThat(mavenHelperPublic.getCalculatedProjectVersion())
            .isEqualTo("21.15.000-SNAPSHOT");
        assertThat(mavenHelperPublic.getInternalReleaseVersion())
            .isEqualTo("21.15.000");
        assertThat(mavenHelperPublic.getPublicReleaseVersion())
            .isEqualTo("21.15");
    }

    @Test
    void testNextVersions_Public() {
        assertThat(mavenHelperPublic.getInternalNextVersion())
            .isEqualTo("21.15.001-SNAPSHOT");
        assertThat(mavenHelperPublic.getPublicNextVersion())
            .isEqualTo("21.16.001-SNAPSHOT");
    }
}
