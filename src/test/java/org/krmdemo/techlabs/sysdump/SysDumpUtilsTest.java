package org.krmdemo.techlabs.sysdump;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpEnvVars;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpEnvVarsAsJson;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpEnvVarsEx;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpEnvVarsExAsJson;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpSysProps;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpSysPropsAsJson;

/**
 * TODO: cover the indents with tests more properly
 */
public class SysDumpUtilsTest {

    @Test
    void testDumpSysProps() {
        assertThat(dumpSysProps())
            .isNotEmpty()
            .containsKeys(
                "file.separator",
                "sun.java.command",
                "user.dir",
                "user.home"
            );
        System.out.println("dumpSysPropsAsJson() --> " + dumpSysPropsAsJson());
        assertThat(dumpSysPropsAsJson())
            .matches("(?s)^\\{.*\"file\\.separator\".*}$")
            .matches("(?s)^\\{.*\"sun\\.java\\.command\".*}$")
            .matches("(?s)^\\{.*\"user\\.dir\".*}$")
            .matches("(?s)^\\{.*\"user\\.home\".*}$");
    }

    @Test
    void testDumpEnv() {
        assertThat(dumpEnvVars())
            .isNotEmpty()
            .containsKeys(
                "PATH",
                "PWD",
                "USER"
            );
        assertThat(dumpEnvVars().get("PATH")).isInstanceOf(String.class);
        System.out.println("dumpEnvVarsAsJson() --> " + dumpEnvVarsAsJson());
        assertThat(dumpEnvVarsAsJson())
            .matches("(?s)^\\{.*\"PATH\".*}$")
            .matches("(?s)^\\{.*\"PWD\".*}$")
            .matches("(?s)^\\{.*\"USER\".*}$");
    }

    @Test
    void testDumpEnvEx() {
        assertThat(dumpEnvVarsEx()).hasSize(dumpEnvVars().size());
        assertThat(dumpEnvVarsEx().get("PATH")).isInstanceOf(List.class);
        System.out.println("dumpEnvVarsExAsJson() --> " + dumpEnvVarsExAsJson());
        assertThat(dumpEnvVarsExAsJson())
            .matches("(?s)^\\{.*\"PATH\": \\[.*].*}$")
            .matches("(?s)^\\{.*\"PWD\".*}$")
            .matches("(?s)^\\{.*\"USER\".*}$");
    }
}
