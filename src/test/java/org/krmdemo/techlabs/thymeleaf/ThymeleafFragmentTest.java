package org.krmdemo.techlabs.thymeleaf;

import lombok.Getter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.utils.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This unit-test is just a sandbox for using Thymeleaf-Engine for conditions and fragments
 */
@Getter  // <-- this one is necessary fpr Thymeleaf-template to access properties via getters
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ThymeleafFragmentTest {

    boolean renderingFragmentA = true;
    boolean renderingFragmentB = false;

    @Test
    void testConditionValues() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String renderingProps = templateEngine.process("""
            renderingFragmentA = [[${this.renderingFragmentA}]];
            renderingFragmentB = [[${this.renderingFragmentB}]];
            """, ctx);
        //System.out.printf("renderingProps:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", renderingProps);
        assertThat(renderingProps).isEqualTo("""
            renderingFragmentA = true;
            renderingFragmentB = false;
            """);
    }

    @Test
    void testConditionalContent_OnlyA() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String conditionalContent = templateEngine.process("""
            <th:block th:if="${this.renderingFragmentA}">Something related to 'A' is here !!!</th:block>
            <th:block th:if="${this.renderingFragmentB}">Something related to 'B' is here !!!</th:block>
            """, ctx);
        //System.out.printf("renderingProps:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", conditionalContent);
        assertThat(conditionalContent).isEqualTo("""
            Something related to 'A' is here !!!
            
            """);
    }

    @Test
    void testConditionalContent_OnlyB() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        this.renderingFragmentA = false;
        this.renderingFragmentB = true;
        String conditionalContent = templateEngine.process("""
            <th:block th:if="${this.renderingFragmentA}">Something related to 'A' is here !!!</th:block>
            <th:block th:if="${this.renderingFragmentB}">Something related to 'B' is here !!!</th:block>
            """, ctx);
        //System.out.printf("renderingProps:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", conditionalContent);
        assertThat(conditionalContent).isEqualTo("""
            
            Something related to 'B' is here !!!
            """);
    }

    @Test
    void testConditionalContent_UsingUnless() {
        TemplateEngine templateEngine = new TemplateEngine();
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String conditionalContent = templateEngine.process("""
            <th:block th:unless="${this.renderingFragmentA}">Something related to '!A' is here !!!</th:block>
            <th:block th:unless="${this.renderingFragmentB}">Something related to '!B' is here !!!</th:block>
            """, ctx);
       // System.out.printf("renderingProps:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", conditionalContent);
        assertThat(conditionalContent).isEqualTo("""
            
            Something related to '!B' is here !!!
            """);
    }
}
