package org.krmdemo.techlabs.core.dump;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.render.OuterTagUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test to verify the way how SVG-image is embedded into IMG-tag
 */
public class EmbeddedSvgTest {

    @Test
    void test_sample_01() {
        String outerSvgTag = resourceContent("sample--01-fill.svg");
        String htmlDoc = OuterTagUtils.outerHtml(outerSvgTag,"Result of 'test_sample_01'");
        assertThat(htmlDoc).isEqualTo(resourceContent("sample--01-fill.svg.html"));
        String htmlImgDoc = OuterTagUtils.outerHtml(
            OuterTagUtils.embeddedSvgTag(outerSvgTag),
            "Result of 'test_sample_01' (embedded SVG)"
        );
        assertThat(htmlImgDoc).isEqualTo(resourceContent("sample--01-fill.svg.img.html"));
    }

    @Test
    void test_sample_02() {
        String outerSvgTag = resourceContent("sample--02-font.svg");
        String htmlDoc = OuterTagUtils.outerHtml(outerSvgTag,"Result of 'test_sample_02'");
        assertThat(htmlDoc).isEqualTo(resourceContent("sample--02-font.svg.html"));
        String htmlImgDoc = OuterTagUtils.outerHtml(
            OuterTagUtils.embeddedSvgTag(outerSvgTag),
            "Result of 'test_sample_02' (embedded SVG)"
        );
        assertThat(htmlImgDoc).isEqualTo(resourceContent("sample--02-font.svg.img.html"));
    }

    private String resourceContent(String sampleSvgName) {
        String resourcePath = "/svg-samples/" + sampleSvgName;
        URL resourceURL = getClass().getResource(resourcePath);
        assertThat(resourceURL)
            .describedAs(
                String.format("resource of class %s with path '%s'",
                    getClass().getSimpleName(), resourcePath))
            .isNotNull();
        try (InputStream resourceStream = resourceURL.openStream()) {
            return IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not read the content of SVG-sample '%s' for class %s",
                resourcePath, getClass().getSimpleName()), ioEx);
        }
    }
}
