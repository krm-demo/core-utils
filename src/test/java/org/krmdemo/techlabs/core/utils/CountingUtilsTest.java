package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CountingUtilsTest {

    @Test
    void testCreationIsProhibited() {
        UnsupportedOperationException uoEx = assertThrows(UnsupportedOperationException.class,
            () -> CorePropsUtils.newInstance(CountingUtils.class)
        );
        assertThat(uoEx.getMessage()).isEqualTo(
            "Cannot instantiate utility-class org.krmdemo.techlabs.core.utils.CountingUtils");
    }
}
