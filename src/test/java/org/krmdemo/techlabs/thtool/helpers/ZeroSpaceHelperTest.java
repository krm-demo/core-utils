package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for {@link ZeroSpaceHelper}
 */
public class ZeroSpaceHelperTest {

    ZeroSpaceHelper zeroSpace = new ZeroSpaceHelper();

    @Test
    void testBlank() {
        assertThat(zeroSpace.mask4(null)).isNull();
        assertThat(zeroSpace.mask4("")).isEmpty();
        assertThat(zeroSpace.mask4("  \t\n ")).isEqualTo("  \t\n ");
    }

    @Test
    void testRangeViolation() {
        String str10 = "1234567890";
        assertThat(zeroSpace.insertInto(str10, 0)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, -10)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, -11)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, -111)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, 10)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, 11)).isEqualTo(str10);
        assertThat(zeroSpace.insertInto(str10, 111)).isEqualTo(str10);
    }

    @Test
    void testDefault() {
        assertThat(zeroSpace.mask4("1")).isEqualTo("1");
        assertThat(zeroSpace.mask4("12")).isEqualTo("12");
        assertThat(zeroSpace.mask4("123")).isEqualTo("123");
        assertThat(zeroSpace.mask4("1234")).isEqualTo("1234");

        assertThat(zeroSpace.mask4("12345")).isEqualTo("1234&#8203;5");
        assertThat(zeroSpace.mask4("123456")).isEqualTo("1234&#8203;56");
        assertThat(zeroSpace.mask4("1234567")).isEqualTo("1234&#8203;567");
        assertThat(zeroSpace.mask4("12345678")).isEqualTo("1234&#8203;5678");

        assertThat(zeroSpace.mask4("123456789")).isEqualTo("1234&#8203;5&#8203;6789");
        assertThat(zeroSpace.mask4("1234567890")).isEqualTo("1234&#8203;56&#8203;7890");

        assertThat(zeroSpace.mask4("package org.krmdemo.techlabs.thtool;"))
            .isEqualTo("pack&#8203;age org.krmdemo.techlabs.tht&#8203;ool;");
    }

    @Test
    void testUseLiteral() {
        assertThat(zeroSpace.useLiteral().mask4("1")).isEqualTo("1");
        assertThat(zeroSpace.useLiteral().mask4("12")).isEqualTo("12");
        assertThat(zeroSpace.useLiteral().mask4("123")).isEqualTo("123");
        assertThat(zeroSpace.useLiteral().mask4("1234")).isEqualTo("1234");

        assertThat(zeroSpace.useLiteral().mask4("12345")).isEqualTo("1234&ZeroWidthSpace;5");
        assertThat(zeroSpace.useLiteral().mask4("123456")).isEqualTo("1234&ZeroWidthSpace;56");
        assertThat(zeroSpace.useLiteral().mask4("1234567")).isEqualTo("1234&ZeroWidthSpace;567");
        assertThat(zeroSpace.useLiteral().mask4("12345678")).isEqualTo("1234&ZeroWidthSpace;5678");

        assertThat(zeroSpace.useLiteral().mask4("123456789")).isEqualTo("1234&ZeroWidthSpace;5&ZeroWidthSpace;6789");
        assertThat(zeroSpace.useLiteral().mask4("1234567890")).isEqualTo("1234&ZeroWidthSpace;56&ZeroWidthSpace;7890");

        assertThat(zeroSpace.useLiteral().mask4("package org.krmdemo.techlabs.thtool;"))
            .isEqualTo("pack&ZeroWidthSpace;age org.krmdemo.techlabs.tht&ZeroWidthSpace;ool;");
    }

    @Test
    void testUseHex() {
        assertThat(zeroSpace.useHex().mask4("1")).isEqualTo("1");
        assertThat(zeroSpace.useHex().mask4("12")).isEqualTo("12");
        assertThat(zeroSpace.useHex().mask4("123")).isEqualTo("123");
        assertThat(zeroSpace.useHex().mask4("1234")).isEqualTo("1234");

        assertThat(zeroSpace.useHex().mask4("12345")).isEqualTo("1234&#x200B;5");
        assertThat(zeroSpace.useHex().mask4("123456")).isEqualTo("1234&#x200B;56");
        assertThat(zeroSpace.useHex().mask4("1234567")).isEqualTo("1234&#x200B;567");
        assertThat(zeroSpace.useHex().mask4("12345678")).isEqualTo("1234&#x200B;5678");

        assertThat(zeroSpace.useHex().mask4("123456789")).isEqualTo("1234&#x200B;5&#x200B;6789");
        assertThat(zeroSpace.useHex().mask4("1234567890")).isEqualTo("1234&#x200B;56&#x200B;7890");

        assertThat(zeroSpace.useHex().mask4("package org.krmdemo.techlabs.thtool;"))
            .isEqualTo("pack&#x200B;age org.krmdemo.techlabs.tht&#x200B;ool;");
    }
}
