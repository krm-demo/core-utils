package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatReflectiveOperationException;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A unit-test to verify the utility-class {@link CorePropsUtils}
 */
public class CorePropsUtilsTest {

    @Test
    void testNewInstance() {
        assertThat(CorePropsUtils.newInstance(String.class)).isEqualTo("");
        assertThatIllegalArgumentException().isThrownBy(
            () -> CorePropsUtils.newInstance(Integer.class)
        ).withCauseInstanceOf(NoSuchMethodException.class);
    }

    @Test
    void testCreationIsProhibited() {
        UnsupportedOperationException uoEx = assertThrows(UnsupportedOperationException.class,
            () -> CorePropsUtils.newInstance(CorePropsUtils.class)
        );
        assertThat(uoEx.getMessage()).isEqualTo(
            "Cannot instantiate utility-class org.krmdemo.techlabs.core.utils.CorePropsUtils");
    }
}
