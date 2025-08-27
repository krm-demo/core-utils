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
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpSysPropsEx;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.dumpSysPropsExAsJson;

/**
 * TODO: cover the indents with tests more properly
 */
public class SysDumpUtilsTest {

    @Test
    void testDumpSysProps() {
        System.out.println("dumpSysPropsAsJson() --> " + dumpSysPropsAsJson());
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
        System.out.println("dumpSysPropsExAsJson() --> " + dumpSysPropsExAsJson());
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
        System.out.println("dumpEnvVarsAsJson() --> " + dumpEnvVarsAsJson());
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
        System.out.println("dumpEnvVarsExAsJson() --> " + dumpEnvVarsExAsJson());
        assertThat(dumpEnvVarsEx()).hasSize(dumpEnvVars().size());
        assertThat(dumpEnvVarsEx().get("PATH")).isInstanceOf(List.class);
        assertThat(dumpEnvVarsExAsJson())
            .matches("(?s)^\\{.*\"PATH\": \\[.*].*}$")
            .matches("(?s)^\\{.*\"PWD\".*}$")
            .matches("(?s)^\\{.*\"USER\".*}$");
    }
}
