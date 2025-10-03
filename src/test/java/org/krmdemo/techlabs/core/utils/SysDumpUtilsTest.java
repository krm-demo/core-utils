package org.krmdemo.techlabs.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.helpers.GitHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpEnvVars;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpEnvVarsAsJson;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpEnvVarsEx;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpEnvVarsExAsJson;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpSysProps;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpSysPropsAsJson;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpSysPropsEx;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.dumpSysPropsExAsJson;

/**
 * A unit-test of utility-class {@link SysDumpUtils}.
 */
@Slf4j
public class SysDumpUtilsTest {

    @Test
    void testDumpSysProps() {
        log.debug("dumpSysPropsAsJson() --> " + dumpSysPropsAsJson());
        assertThat(dumpSysProps())
            .isNotEmpty()
            .containsKeys(
                "file.separator",
                "sun.java.command",
                "user.dir",
                "user.home"
            );
        assertThat(dumpSysPropsAsJson())
            .matches("(?s)^\\{.*\"file\\.separator\".*}$")
            .matches("(?s)^\\{.*\"sun\\.java\\.command\".*}$")
            .matches("(?s)^\\{.*\"user\\.dir\".*}$")
            .matches("(?s)^\\{.*\"user\\.home\".*}$");
    }

    @Test
    void testDumpSysPropsEx() {
        log.debug("dumpSysPropsExAsJson() --> " + dumpSysPropsExAsJson());
        assertThat(dumpSysPropsEx()).hasSize(dumpSysProps().size());
        assertThat(dumpSysPropsEx().get("java.class.path")).isInstanceOf(List.class);
        assertThat(dumpSysPropsExAsJson())
            .matches("(?s)^\\{.*\"java\\.class\\.path\": \\[.*].*}$")
            .matches("(?s)^\\{.*\"sun\\.java\\.command\".*}$")
            .matches("(?s)^\\{.*\"user\\.dir\".*}$")
            .matches("(?s)^\\{.*\"user\\.home\".*}$");
    }

    @Test
    void testDumpEnv() {
        log.debug("dumpEnvVarsAsJson() --> " + dumpEnvVarsAsJson());
        assertThat(dumpEnvVars())
            .isNotEmpty()
            .containsKeys(
                "PATH",
                "PWD",
                "USER"
            );
        assertThat(dumpEnvVars().get("PATH")).isInstanceOf(String.class);
        assertThat(dumpEnvVarsAsJson())
            .matches("(?s)^\\{.*\"PATH\".*}$")
            .matches("(?s)^\\{.*\"PWD\".*}$")
            .matches("(?s)^\\{.*\"USER\".*}$");
    }

    @Test
    void testDumpEnvEx() {
        log.debug("dumpEnvVarsExAsJson() --> " + dumpEnvVarsExAsJson());
        assertThat(dumpEnvVarsEx()).hasSize(dumpEnvVars().size());
        assertThat(dumpEnvVarsEx().get("PATH")).isInstanceOf(List.class);
        assertThat(dumpEnvVarsExAsJson())
            .matches("(?s)^\\{.*\"PATH\": \\[.*].*}$")
            .matches("(?s)^\\{.*\"PWD\".*}$")
            .matches("(?s)^\\{.*\"USER\".*}$");
    }
}
