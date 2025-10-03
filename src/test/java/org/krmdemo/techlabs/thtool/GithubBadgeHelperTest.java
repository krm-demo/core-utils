package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test to <b>{@code th-tool}</b>-helper {@link GithubBadgeHelper}.
 */
public class GithubBadgeHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testBadgePassing() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(gh.propsGithub()).isNotEmpty();
        gh.propsGithub().put("workflow", "expected-workflow-name");
        assertThat(gh.getWorkflowName()).isEqualTo("expected-workflow-name");
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.getBadgeBuildPassing()).isEqualTo("""
            [![expected-workflow-name](https://github.com/krm-demo/core-utils/actions/workflows/expected-workflow-name.yml/badge.svg?event=push)](https://github.com/krm-demo/core-utils/actions/workflows/expected-workflow-name.yml)""");
    }
}
