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
    }
}
