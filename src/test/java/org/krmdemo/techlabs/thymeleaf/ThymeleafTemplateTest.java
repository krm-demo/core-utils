package org.krmdemo.techlabs.thymeleaf;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.utils.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapResource;

/**
 * This unit-test is just a sandbox for using Thymeleaf-Engine
 * <hr/>
 * TODO: verify the usage of Grape, JBang-DEPS and JBang-Bash
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
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
    void testMarkdown_UsageMaven() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context mavenPropsContext = new Context(Locale.getDefault());
        mavenPropsContext.setVariable("this", this);
        mavenPropsContext.setVariable(
            "mavenProps",
            propsMapResource(RESOURCE__MAVEN_PROPS)
        );
        String mavenUsageMD = templateEngine.process("""
            for main-source dependencies:
            ```XML
                <dependency>
                    <groupId>[[${mavenProps.get("maven-project.group")}]]</groupId>
                    <artifactId>[[${mavenProps.get("maven-project.artifact")}]]</artifactId>
                    <version>[[${mavenProps.get("maven-project.version")}]]</artifactId>
                </dependency>
            ```
            for test-source dependencies:
            ```XML
                <dependency>
                    <groupId>[[${mavenProps.get("maven-project.group")}]]</groupId>
                    <artifactId>[[${mavenProps.get("maven-project.artifact")}]]</artifactId>
                    <version>[[${mavenProps.get("maven-project.version")}]]</artifactId>
                    <scope>test</scope>
                </dependency>
            ```
            """, mavenPropsContext);
        System.out.printf("mavenUsageMD:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenUsageMD);
    }

    @Test
    void testMarkdown_UsageGradle() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context mavenPropsContext = new Context(Locale.getDefault());
        mavenPropsContext.setVariable("this", this);
        mavenPropsContext.setVariable(
            "mavenProps",
            propsMapResource(RESOURCE__MAVEN_PROPS)
        );
        String gradleUsageMD = templateEngine.process("""
            ```Gradle
                // for main-source implementation dependencies:
                implementation("[[${mavenProps.get("maven-project.group")}]]:[[${mavenProps.get("maven-project.artifact")}]]:[[${mavenProps.get("maven-project.version")}]]")
                . . . . . . . . . . . . . .
                // for test-source implementation dependencies:
                testImplementation("[[${mavenProps.get("maven-project.group")}]]:[[${mavenProps.get("maven-project.artifact")}]]:[[${mavenProps.get("maven-project.version")}]]")
            ```
            """, mavenPropsContext);
        System.out.printf("gradleUsageMD:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", gradleUsageMD);
    }
}
