package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.techlabs.thtool.ThToolCtxUtils.propValueStr;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test to <b>{@code th-tool}</b>-helper {@link GithubHelper}.
 */
public class GithubHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testNoVar() {
        GithubHelper gh = GithubHelper.fromCtxLazy();
        assertThat(gh.propsGithub()).isEmpty();
    }

    @Test
    void testWorkflow() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(gh.propsGithub()).isNotEmpty();
        List<String> expectedWorkflowNames = List.of(
            "on-main-push",
            "release-internal",
            "release-public"
        );
        assertThat(expectedWorkflowNames).contains(gh.getWorkflowName());
    }

    @Test
    void testProjectRepoHtmlUrl() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(gh.propsGithub()).isNotEmpty();
        assertThat(gh.getProjectRepoHtmlUrl()).isEqualTo("https://github.com/krm-demo/core-utils");
    }

    @Test
    void testInvalidPropsChain_wrong_first() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(propValueStr(gh.propsGithub(), "wrongPropName")).isNull();
        assertThatIllegalArgumentException().isThrownBy(
            () -> propValueStr(gh.propsGithub(), "wrongPropName", "repository", "html_url")
        ).withMessage(
            "Could not resolve the property-chain 'wrongPropName.repository.html_url' - " +
            "the tail 'repository.html_url' is unresolved " +
            "(the value of 'wrongPropName' is expected to be a Map, but it's null)."
        );
        assertThatIllegalArgumentException().isThrownBy(
            () -> propValueStr(gh.propsGithub(), "workflow", "repository", "html_url")
        ).withMessage(
            "Could not resolve the property-chain 'workflow.repository.html_url' - " +
            "the tail 'repository.html_url' is unresolved " +
            "(the value of 'workflow' is expected to be a Map, but it's of type <<class java.lang.String>>)."
        );
    }

    @Test
    void testInvalidPropsChain_wrong_second() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(propValueStr(gh.propsGithub(), "event", "wrongPropName")).isNull();
        assertThatIllegalArgumentException().isThrownBy(
            () -> propValueStr(gh.propsGithub(), "event", "wrongPropName", "html_url")
        ).withMessage(
            "Could not resolve the property-chain 'event.wrongPropName.html_url' - " +
                "the tail 'html_url' is unresolved " +
                "(the value of 'event.wrongPropName' is expected to be a Map, but it's null)."
        );
        assertThatIllegalArgumentException().isThrownBy(
            () -> propValueStr(gh.propsGithub(), "event", "ref", "html_url")
        ).withMessage(
            "Could not resolve the property-chain 'event.ref.html_url' - " +
            "the tail 'html_url' is unresolved " +
            "(the value of 'event.ref' is expected to be a Map, but it's of type <<class java.lang.String>>)."
        );
    }

    @Test
    void testInvalidPropsChain_wrong_third() {
        GithubHelper gh = GithubHelper.fromCtxLazy(ttCtx);
        assertThat(propValueStr(gh.propsGithub(), "event", "repository", "wrongPropName")).isNull();
    }
}
