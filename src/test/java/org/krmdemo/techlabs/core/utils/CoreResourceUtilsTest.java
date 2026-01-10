package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * A unit-test to verify the utility-class {@link CoreResourceUtils}
 */
public class CoreResourceUtilsTest {

    @Test
    void testResourceAsText() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
            () -> CoreResourceUtils.resourceAsText(null));
        assertThatIllegalArgumentException().isThrownBy(
            () -> CoreResourceUtils.resourceAsText("some-unknown-resource")
        ).withMessageContaining("could not locate teh resource by path 'some-unknown-resource'");
        assertThat(CoreResourceUtils.resourceAsText("vars/var-aaa.json")).isEqualToIgnoringWhitespace("""
            {
              "a": "the value of property 'a'",
              "b": "the value of property 'b'",
              "c": {
                "d": "the value of property 'c.d'",
                "e": "the value of property 'c.e'"
              },
              "seq" : ["1", "2", "3"]
            }""");
    }

    @Test
    void testResourceAsTextOpt() {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(
            () -> CoreResourceUtils.resourceAsText(null));
        assertThat(CoreResourceUtils.resourceAsTextOpt("some-unknown-resource")).isEmpty();
        Optional<String> textOpt = CoreResourceUtils.resourceAsTextOpt("vars/var-bbb.json");
        assertThat(textOpt.orElseThrow()).isEqualToIgnoringWhitespace("""
            [
              {
                "a": "the value of property '[0].a'",
                "b": "the value of property '[0].b'"
              },
              "the value of element '[1]'",
              "the value of element '[2]'",
              [
                "the value of element '[3][0]'",
                "the value of element '[3][1]'"
              ]
            ]""");
    }

    @Test
    void testResourceAsTextList() {
        List<String> textList = CoreResourceUtils.resourceAsTextList(
            "vars/var-aaa.json", "some-unknown-resource", "vars/var-bbb.json");
        assertThat(textList).hasSize(2);
    }
}
