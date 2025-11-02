package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.dump.render.Highlight;
import org.krmdemo.techlabs.core.dump.render.RenderSpec;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link CoreUtilsHelper}.
 * <hr/>
 * Here is a good example of mocking the static-methods, which is one of the latest feature of <b>mockito</b>-library.
 *
 * @see <a href="https://www.javadoc.io/static/org.mockito/mockito-core/5.20.0/org.mockito/org/mockito/Mockito.html#48">
 *     (Mockito JavaDoc) 48. Mocking static methods
 * </a>
 * @see <a href="https://www.baeldung.com/mockito-mock-static-methods">
 *     (Baeldung) Mocking Static Methods With <code>Mockito</code>
 * </a>
 */
public class CoreUtilsHelperTest {

    private final CoreUtilsHelper cu = new CoreUtilsHelper();

    @Test
    void testDelegationTo_DumpUtils() {
        try (MockedStatic<DumpUtils> mockedDumpUtils = mockStatic(DumpUtils.class)) {
            // register mocking expectations:
            mockedDumpUtils.when(() -> DumpUtils.dumpAsJsonTxt(any()))
                .thenReturn("some-result for 'dumpAsJsonTxt(Object)'");
            mockedDumpUtils.when(() -> DumpUtils.dumpAsJsonTxt(any(), any(RenderSpec.class)))
                .thenReturn("some-result for 'dumpAsJsonTxt(Object,RenderSpec)'");
            // perform the invocation of our helper and assert the expected delegations:
            assertThat(cu.dumpAsJsonTxt(List.of(1, 2, 3)))
                .isEqualTo("some-result for 'dumpAsJsonTxt(Object)'");
            assertThat(cu.dumpAsJsonTxt(List.of(1, 2, 3), new RenderSpec(Highlight.DEFAULT)))
                .isEqualTo("some-result for 'dumpAsJsonTxt(Object,RenderSpec)'");
        }
    }

    @Test
    void testDelegationTo_DateTimeUtils() {
        DateTimeTriplet dttNow = DateTimeTriplet.now(); // <-- there could be any predefined value to assert
        try (MockedStatic<CoreDateTimeUtils> mockedDumpUtils = mockStatic(CoreDateTimeUtils.class)) {
            // register mocking expectations:
            mockedDumpUtils.when(() -> CoreDateTimeUtils.dtt(anyString())).thenReturn(dttNow);
            // perform the invocation of our helper and assert the expected delegations:
            assertThat(cu.dtt("2007-12-03T10:15:30.00Z")).isEqualTo(dttNow);
        }
    }
}
