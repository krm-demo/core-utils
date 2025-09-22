package org.krmdemo.techlabs.dump;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.dump.render.OuterTagUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedSvgTest {

    @Test
    void test_sample_01() {
        String outerSvgTag = resourceContent("sample--01-fill.json.svg");
        String htmlDoc = OuterTagUtils.outerHtml(outerSvgTag,"Result of 'test_sample_01'");
        System.out.println(htmlDoc);
    }

    @Test
    void test_sample_02() {
        String outerSvgTag = resourceContent("sample--02-font.json.svg");
        String htmlDoc = OuterTagUtils.outerHtml(outerSvgTag,"Result of 'test_sample_02'");
        System.out.println(htmlDoc);
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
