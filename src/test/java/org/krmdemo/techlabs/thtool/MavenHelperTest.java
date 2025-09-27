package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for {@link MavenHelper}
 */
public class MavenHelperTest {

    private final MavenHelper mavenHelper = new MavenHelper(
        "/org/krmdemo/techlabs/core/thtool/test-01--maven-project.properties");

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
            .isEqualTo("21.01.000-SNAPSHOT");
    }
}
