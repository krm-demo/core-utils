package org.krmdemo.techlabs.thymeleaf;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.utils.SysDumpUtils;
import org.krmdemo.techlabs.thtool.helpers.MavenHelper;
import org.krmdemo.techlabs.thtool.helpers.ZeroSpaceHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapResource;

/**
 * This unit-test is just a sandbox for using Thymeleaf-Engine for expression and iterations
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ThymeleafTemplateTest {

    public static final String RESOURCE__MAVEN_PROPS = "/META-INF/maven/test-project-group/test-project-artifact01/maven-project.properties";

    @SuppressWarnings("unused")  // <-- used inside template !!!
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
                    </tr>
                </tbody>
            </table>
            """, envVarsContext);
        //System.out.printf("envVarsHTML:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", envVarsHTML);
        assertThat(envVarsHTML)
            .isNotBlank()
            .startsWith("<table>")
            .contains("<td>HOME</td>")
            .contains("<td>PATH</td>")
            .contains("<td>USER</td>")
            .endsWith("</table>\n");
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
        //System.out.printf("mavenPropsHTML:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenPropsHTML);
        String mavenPropsNoGettersHTML = templateEngine.process("""
            <table>
                <thead>
                    <tr>
                        <th>Maven Build-Property Name</th>
                        <th>Maven BuildProperty Value</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="propEntry: ${mavenProps.entrySet()}">
                        <td th:text="${propEntry.key}"> ...prop-name... </td>
                        <td th:text="${propEntry.value}"> ...prop-value... </td>
                    </tr>
                </tbody>
            </table>
            """, mavenPropsContext);
        //System.out.printf("mavenPropsHTML:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenPropsNoGettersHTML);
        assertThat(mavenPropsHTML).isEqualTo(mavenPropsNoGettersHTML);
        assertThat(mavenPropsHTML)
            .isNotBlank()
            .startsWith("<table>")
            .contains("<td>maven-project.build-date-time</td>")
            .contains("<td>maven-project.version</td>")
            .containsPattern("<td>maven-project\\.artifact</td>\\s*<td>core-utils</td>")
            .endsWith("</table>\n");
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
        //System.out.printf("mavenUsageMD:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mavenUsageMD);
        assertThat(mavenUsageMD)
            .isNotBlank()
            .startsWith("for main-source dependencies:")
            .contains("core-utils")
            .containsPattern("(```XML[^`]*```\\s*)[^`]*(```XML[^`]*<scope>test</scope>[^`]*```\\s*)")
            .endsWith("```\n");
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
        //System.out.printf("gradleUsageMD:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", gradleUsageMD);
        assertThat(gradleUsageMD)
            .isNotBlank()
            .startsWith("```Gradle")
            .contains("core-utils")
            .containsPattern("```Gradle[^`]*```\\s*")
            .endsWith("```\n");
    }

    @Test
    void testZeroSpaceHelper() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("zh", new ZeroSpaceHelper());
        ctx.setVariable("mh", new MavenHelper());
        // here the difference between Thymeleaf-Inlines [[...]] and [(..)] is demonstrated:
        String mvnProps = templateEngine.process("""
            mh.resourcePath = "[[${mh.resourcePath}]]";  // <-- escaped
            mh.resourcePath = "[(${mh.resourcePath})]";  // <-- un-escaped
            zh.mask4(mh.resourcePath) = "[[${zh.mask4(mh.resourcePath)}]]";  // <-- escaped
            zh.mask4(mh.resourcePath) = "[(${zh.mask4(mh.resourcePath)})]";  // <-- un-escaped
            """, ctx);
        //System.out.printf("mvnProps:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", mvnProps);
        assertThat(mvnProps).isEqualTo("""
            mh.resourcePath = "/META-INF/maven/io.github.krm-demo/core-utils/maven-project.properties";  // <-- escaped
            mh.resourcePath = "/META-INF/maven/io.github.krm-demo/core-utils/maven-project.properties";  // <-- un-escaped
            zh.mask4(mh.resourcePath) = "/MET&amp;#8203;A-INF/maven/io.github.krm-demo/core-utils/maven-project.proper&amp;#8203;ties";  // <-- escaped
            zh.mask4(mh.resourcePath) = "/MET&#8203;A-INF/maven/io.github.krm-demo/core-utils/maven-project.proper&#8203;ties";  // <-- un-escaped
            """);
    }
}
