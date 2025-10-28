package org.krmdemo.techlabs.jacoco;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.utils.CorePropsUtils;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link JacocoHelper}.
 */
public class JacocoHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testBadgeHtml() {
        JacocoHelper jacoco = JacocoHelper.fromCtxLazy(ttCtx);
        assertThat(jacoco.getBadgeHtml()).contains("jacoco-reports");
    }

    @Test
    void testBadgeMarkdown() {
        JacocoHelper jacoco = JacocoHelper.fromCtxLazy(ttCtx);
        assertThat(jacoco.getBadgeMarkdown()).contains("[JaCoCo]");
    }

    @Test
    void testBadgeValue() {
        JacocoHelper jacoco = JacocoHelper.fromCtxLazy(ttCtx);
        System.out.println(jacoco);
        if (jacoco.getJacocoCounter().isEmpty()) {
            assertThat(jacoco.getBadgeValue()).isEqualTo(JacocoCounter.NO_VALUE_STR);
            assertThat(jacoco.getBadgeTooltip()).isEmpty();
        } else {
            assertThat(jacoco.getBadgeValue()).matches("\\d?\\d?\\d.\\d\\d");
            assertThat(jacoco.getBadgeTooltip()).startsWith("JaCoCo LINE:");
        }
    }
}
