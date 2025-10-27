package org.krmdemo.techlabs.jacoco;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

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
        JacocoHelper openTest4j = JacocoHelper.fromCtxLazy(ttCtx);
        assertThat(openTest4j.getBadgeHtml()).contains("jacoco-reports");
    }

    @Test
    void testBadgeMarkdown() {
        JacocoHelper openTest4j = JacocoHelper.fromCtxLazy(ttCtx);
        assertThat(openTest4j.getBadgeMarkdown()).contains("[JaCoCo]");
    }
}
