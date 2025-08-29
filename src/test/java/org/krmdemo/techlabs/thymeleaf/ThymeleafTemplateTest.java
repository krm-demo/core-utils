package org.krmdemo.techlabs.thymeleaf;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.stream.TechlabsStreamUtils;
import org.krmdemo.techlabs.sysdump.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.sysdump.PropertiesUtils.propsMapResource;

public class ThymeleafTemplateTest {

    public final static String RESOURCE__MAVEN_PROPS = "/META-INF/maven/maven-project.properties";

    public boolean isIterable(Object someObject) {
        return someObject instanceof Iterable;
    }

    @Test
    void testHTML_EnvVars() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context envVarsContext = new Context(Locale.getDefault());
        envVarsContext.setVariable("this", this);
        envVarsContext.setVariable(
            "envVarsMap",
            SysDumpUtils.dumpEnvVarsEx()
        );
        String envVarsHTML = templateEngine.process("""
            <table>
                <thead>
                    <tr>
                        <th>Env-Var Name</th>
                        <th>Env-Var Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="envVar: ${envVarsMap.entrySet()}">
                        <td th:text="${envVar.getKey()}"> ...name... </td>
                        <td th:unless="${this.isIterable(envVar.getValue())}" th:text="${envVar.getValue()}">
                            ...value...
                        </td>
                        <td th:if="${this.isIterable(envVar.getValue())}">
                            <ul>
                                <li th:each="item: ${envVar.getValue()}" th:text="${item}">... item value ...</li>
                            </ul>
                        </td>
                    </ul>
                    </th:block>
                    </tr>
                </tbody>
            </table>
            """, envVarsContext);
        System.out.printf("envVarsHTML:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", envVarsHTML);
    }

    @Test
    void testHTML_MavenProps() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context mavenPropsContext = new Context(Locale.getDefault());
        mavenPropsContext.setVariable("this", this);
        mavenPropsContext.setVariable(
            "mavenProps",
            propsMapResource(RESOURCE__MAVEN_PROPS)
        );
        String mavenPropsHTML = templateEngine.process("""
            <table>
                <thead>
                    <tr>
                        <th>Maven Build-Property Name</th>
                        <th>Maven BuildProperty Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="propEntry: ${mavenProps.entrySet()}">
                        <td th:text="${propEntry.getKey()}"> ...prop-name... </td>
                        <td th:text="${propEntry.getValue()}"> ...prop-value... </td>
                    </tr>
                </tbody>
            </table>
            """, mavenPropsContext);
        System.out.printf("mavenPropsHTML:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenPropsHTML);
    }

    @Test
    void testMarkdown_UsageMaven() throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        Context mavenPropsContext = new Context(Locale.getDefault());
        mavenPropsContext.setVariable("this", this);
        mavenPropsContext.setVariable(
            "mavenProps",
            propsMapResource(RESOURCE__MAVEN_PROPS)
        );
        String mavenUsageMD = templateEngine.process("""
            ```XML
                <dependency>
                    <groupId>[[${mavenProps.get("maven-project.group")}]]</groupId>
                    <artifactId>[[${mavenProps.get("maven-project.artifact")}]]</artifactId>
                    <version>[[${mavenProps.get("maven-project.version")}]]</artifactId>
                </dependency>
            ```
            """, mavenPropsContext);
        System.out.printf("mavenUsageMD:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenUsageMD);
    }
}
