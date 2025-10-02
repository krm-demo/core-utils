package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_NAME__GITHUB_INPUTS;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_NAME__RELEASE_PHASE;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_NAME__RELEASE_TYPE;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_VALUE__PHASE_MAIN;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_VALUE__PHASE_NEXT;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_VALUE__RELEASE_TYPE_INTERNAL;
import static org.krmdemo.techlabs.thtool.GithubInputsHelper.VAR_PROP_VALUE__RELEASE_TYPE_PUBLIC;

/**
 * A unit-test to {@link GithubInputsHelper}
 */
public class GithubInputsHelperTest {

    @Test
    void testNoVar() {
        GithubInputsHelper gih = new GithubInputsHelper(new ThymeleafToolCtx());
        assertThat(gih.getGithubInputs()).isEmpty();
        assertThat(gih.isReleasing()).isFalse();
        assertThat(gih.isReleasingInternal()).isFalse();
        assertThat(gih.isReleasingPublic()).isFalse();
        assertThat(gih.isRenderingMainPhase()).isFalse();
        assertThat(gih.isRenderingNextPhase()).isFalse();
        assertThat("" + gih).isEqualTo("""
            {
              "githubInputs": {},
              "releasing": "false",
              "releasingInternal": "false",
              "releasingPublic": "false",
              "renderingMainPhase": "false",
              "renderingNextPhase": "false"
            }""");
    }

    @Test
    void testReleaseInternalMain() {
        ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
        ttCtx.setVariable(VAR_NAME__GITHUB_INPUTS, linkedMap(
            nameValue(VAR_PROP_NAME__RELEASE_TYPE, VAR_PROP_VALUE__RELEASE_TYPE_INTERNAL),
            nameValue(VAR_PROP_NAME__RELEASE_PHASE, VAR_PROP_VALUE__PHASE_MAIN)
        ));
        GithubInputsHelper gih = new GithubInputsHelper(ttCtx);
        assertThat(gih.getGithubInputs()).isNotEmpty();
        assertThat(gih.isReleasing()).isTrue();
        assertThat(gih.isReleasingInternal()).isTrue();
        assertThat(gih.isReleasingPublic()).isFalse();
        assertThat(gih.isRenderingMainPhase()).isTrue();
        assertThat(gih.isRenderingNextPhase()).isFalse();
        assertThat("" + gih).isEqualTo("""
            {
              "githubInputs": {
                "release_type": "INTERNAL",
                "release_phase": "call-main"
              },
              "releasing": "true",
              "releasingInternal": "true",
              "releasingPublic": "false",
              "renderingMainPhase": "true",
              "renderingNextPhase": "false"
            }""");
    }

    @Test
    void testReleasePublicMain() {
        ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
        ttCtx.setVariable(VAR_NAME__GITHUB_INPUTS, linkedMap(
            nameValue(VAR_PROP_NAME__RELEASE_TYPE, VAR_PROP_VALUE__RELEASE_TYPE_PUBLIC),
            nameValue(VAR_PROP_NAME__RELEASE_PHASE, VAR_PROP_VALUE__PHASE_MAIN)
        ));
        GithubInputsHelper gih = new GithubInputsHelper(ttCtx);
        assertThat(gih.getGithubInputs()).isNotEmpty();
        assertThat(gih.isReleasing()).isTrue();
        assertThat(gih.isReleasingInternal()).isFalse();
        assertThat(gih.isReleasingPublic()).isTrue();
        assertThat(gih.isRenderingMainPhase()).isTrue();
        assertThat(gih.isRenderingNextPhase()).isFalse();
        assertThat("" + gih).isEqualTo("""
            {
              "githubInputs": {
                "release_type": "PUBLIC",
                "release_phase": "call-main"
              },
              "releasing": "true",
              "releasingInternal": "false",
              "releasingPublic": "true",
              "renderingMainPhase": "true",
              "renderingNextPhase": "false"
            }""");
    }

    @Test
    void testReleaseInternalNext() {
        ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
        ttCtx.setVariable(VAR_NAME__GITHUB_INPUTS, linkedMap(
            nameValue(VAR_PROP_NAME__RELEASE_TYPE, VAR_PROP_VALUE__RELEASE_TYPE_INTERNAL),
            nameValue(VAR_PROP_NAME__RELEASE_PHASE, VAR_PROP_VALUE__PHASE_NEXT)
        ));
        GithubInputsHelper gih = new GithubInputsHelper(ttCtx);
        assertThat(gih.getGithubInputs()).isNotEmpty();
        assertThat(gih.isReleasing()).isTrue();
        assertThat(gih.isReleasingInternal()).isTrue();
        assertThat(gih.isReleasingPublic()).isFalse();
        assertThat(gih.isRenderingMainPhase()).isFalse();
        assertThat(gih.isRenderingNextPhase()).isTrue();
        assertThat("" + gih).isEqualTo("""
            {
              "githubInputs": {
                "release_type": "INTERNAL",
                "release_phase": "call-main-next"
              },
              "releasing": "true",
              "releasingInternal": "true",
              "releasingPublic": "false",
              "renderingMainPhase": "false",
              "renderingNextPhase": "true"
            }""");
    }

    @Test
    void testReleasePublicNext() {
        ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
        ttCtx.setVariable(VAR_NAME__GITHUB_INPUTS, linkedMap(
            nameValue(VAR_PROP_NAME__RELEASE_TYPE, VAR_PROP_VALUE__RELEASE_TYPE_PUBLIC),
            nameValue(VAR_PROP_NAME__RELEASE_PHASE, VAR_PROP_VALUE__PHASE_NEXT)
        ));
        GithubInputsHelper gih = new GithubInputsHelper(ttCtx);
        assertThat(gih.getGithubInputs()).isNotEmpty();
        assertThat(gih.isReleasing()).isTrue();
        assertThat(gih.isReleasingInternal()).isFalse();
        assertThat(gih.isReleasingPublic()).isTrue();
        assertThat(gih.isRenderingMainPhase()).isFalse();
        assertThat(gih.isRenderingNextPhase()).isTrue();
        assertThat("" + gih).isEqualTo("""
            {
              "githubInputs": {
                "release_type": "PUBLIC",
                "release_phase": "call-main-next"
              },
              "releasing": "true",
              "releasingInternal": "false",
              "releasingPublic": "true",
              "renderingMainPhase": "false",
              "renderingNextPhase": "true"
            }""");
    }
}
