package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

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
        assertThat(gh.getWorkflowName()).isEqualTo("on-main-push");

        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.getBadgeBuildPassing()).isEqualTo("""
            [![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)""");
    }
}
