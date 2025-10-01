package org.krmdemo.techlabs.thymeleaf;

import lombok.Getter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This unit-test is just a sandbox for using Thymeleaf-Engine for conditions and fragments
 */
@Getter  // <-- this one is necessary fpr Thymeleaf-template to access properties via getters
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ThymeleafFragmentTest {

    int someIntProp = 1234;

    boolean renderingFragmentA = true;
    boolean renderingFragmentB = false;

    String inlineFragmentA = "test-inline-fragment-A.md.th";
    String inlineFragmentB = "test-inline-fragment-B.md.th";
    String staticFragmentName = "test-static-fragment.md";

    @Test
    void testBlock_FragmentA() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-block/test-block-fragment-A.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This is the block **fragment-`A`**: 2 + 3 = 5""");
    }

    @Test
    void testBlock_FragmentB() {
        this.someIntProp = 345;
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-block/test-block-fragment-B.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This is the block **fragment-`B`**: 3 * someIntProp = 1035""");
    }

    @Test
    void testBlockLayout_NoFragments() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-block/test-block-layout-no-frags.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            There are no fragments in this layout, but the properties are still processed:
            - renderingFragmentA = true;
            - renderingFragmentB = false;
            - someIntProp = 1234;
            """);
    }

    @Test
    void testBlockLayout_IncludeFragments() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-block/test-block-layout-two-frags.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This layout contains two fragments ( someIntProp = 1234 ):
            - This is the block **fragment-`A`**: 2 + 3 = 5
            - This is the block **fragment-`B`**: 3 * someIntProp = 3702
            """);
    }

    // --------------------------------------------------------------------------------------------

    @Test
    void testInline_FragmentA() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-inline/test-inline-fragment-A.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This is the inline **fragment-`A`**: 2 + 3 = 5""");
    }

    @Test
    void testInline_FragmentB() {
        this.someIntProp = 345;
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-inline/test-inline-fragment-B.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This is the inline **fragment-`B`**: 3 * someIntProp = 1035""");
    }

    @Test
    void testInlineLayout_NoFragments() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-inline/test-inline-layout-no-frags.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            There are no fragments in this layout, but the properties are still processed:
            - renderingFragmentA = true;
            - renderingFragmentB = false;
            - someIntProp = 1234;
            """);
    }

    @Test
    void testInlineLayout_IncludeFragments_AsValue() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-inline/test-inline-layout-two-frags-as-value.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This layout contains two inline fragments, included via attr-value ( someIntProp = 1234 ):
            - This is the inline **fragment-`A`**: 2 + 3 = 5
            - This is the inline **fragment-`B`**: 3 * someIntProp = 3702
            """);
    }

    @Test
    void testInlineLayout_IncludeFragments_AsInline() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("this", this);
        String layoutNoFrags = templateEngine.process(".github/th-test-inline/test-inline-layout-two-frags-as-expr.md.th", ctx);
        //System.out.printf("simpleFragments:%n---- ---- ---- ----%n%s---- ---- ---- ----%n", layoutNoFrags);
        assertThat(layoutNoFrags).isEqualTo("""
            This layout contains two inline fragments, included via `~{...}` ( someIntProp = 1234 ):
            - inlineFragmentA = "test-inline-fragment-A.md.th";
            - inlineFragmentB = "test-inline-fragment-B.md.th";
            - staticFragmentName = "test-static-fragment.md";
            - This is the inline **fragment-`A`**: 2 + 3 = 5
            - This is the inline **fragment-`B`**: 3 * someIntProp = 3702
            . . . . .
            - This is the inline **fragment-`A`**: 2 + 3 = 5
            - This is the inline **fragment-`B`**: 3 * someIntProp = 3702
            . . . . .
            - This is the inline **fragment-`A`**: 2 + 3 = 5
            - This is the inline **fragment-`B`**: 3 * someIntProp = 3702
            . . . . . just inline with direct path: . . . . .
            This is a **static-fragment** (from `.github/th-test-inline/test-static-fragment.md`),\s
            that does not contain anything to be process with Thymeleaf-Engine
            . . . . . just inline with calculated path: . . . . .
            - This is a **static-fragment** (from `.github/th-test-inline/test-static-fragment.md`),\s
            that does not contain anything to be process with Thymeleaf-Engine
            . . . . . using some variable in calculation the path: . . . . .
            - This is a **static-fragment** (from `.github/th-test-inline/test-static-fragment.md`),\s
            that does not contain anything to be process with Thymeleaf-Engine
            . . . . .
            """);
    }

    // --------------------------------------------------------------------------------------------

    private static ITemplateResolver templateResolver() {
        // 1. Create a FileTemplateResolver instance
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setOrder(1);
        resolver.setTemplateMode("HTML5");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // <-- disable caching for development, enable for production
        resolver.setCheckExistence(true);  // <-- very important for the chain of resolvers
        return resolver;
    }
}
