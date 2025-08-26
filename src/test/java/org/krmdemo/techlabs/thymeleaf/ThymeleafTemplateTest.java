package org.krmdemo.techlabs.thymeleaf;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.sysdump.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

public class ThymeleafTemplateTest {

    public boolean isIterable(Object someObject) {
        return someObject instanceof Iterable;
    }

    @Test
    void testEnvVarsHTML() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context envVarsContext = new Context(Locale.getDefault());
        envVarsContext.setVariable("this", this);
        envVarsContext.setVariable("envVarsMap", SysDumpUtils.dumpEnvVarsEx());
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
}
