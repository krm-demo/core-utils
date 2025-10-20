package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to be invoked from HTML-pages,
 * which are rendered by standard JDK's <b>{@code javadoc}</b>-tool.
 * <hr/>
 * In this particular project that JDK's tool is invoked using
 * <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/">
 *     Apache Maven Javadoc Plugin
 * </a> and the most important feature that we are using here - is ability to
 * provide custom HTML-fragment for the right-part of top-navigation bar, which is performed
 * in <a href="https://github.com/krm-demo/core-utils/blob/main/pom.xml#L441">{@code 'pom.xml'}</a>
 *
 * @see <a href="https://docs.oracle.com/en/java/javase/21/javadoc/javadoc-tool.html">
 *     (JDK 21) JavaDoc Guide
 * </a>
 * @see <a href="https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html#:~:text=header,mypackage">
 *     The <code>javadoc</code> Command
 * </a>
 */
@JsonPropertyOrder(alphabetic = true)
public class JavaDocHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link JavaDocHelper}
     */
    public static final String VAR_NAME__HELPER = "jdh";

    /**
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link JavaDocHelper} for unit-tests
     */
    public static JavaDocHelper fromCtxLazy(ThymeleafToolCtx ttCtx) {
        JavaDocHelper helper = fromCtx(ttCtx);
        if (helper == null) {
            JavaDocHelper.register(ttCtx);
            GithubBadgeHelper.register(ttCtx);
            GitHelper.register(ttCtx);
            MavenHelper.register(ttCtx);
            helper = fromCtx(ttCtx);
        }
        return helper;
    }

    /**
     * A factory-method that returns an instance of {@link GithubBadgeHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link GithubBadgeHelper} for access from other helpers
     */
    public static JavaDocHelper fromCtx(ThymeleafToolCtx ttCtx) {
        return ttCtx.typedVar(VAR_NAME__HELPER, JavaDocHelper.class);
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependency from <b>{@code th-tool}</b> to helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        JavaDocHelper helper = new JavaDocHelper(ttCtx);
        ttCtx.setVariable(VAR_NAME__HELPER, helper);
    }

    private final ThymeleafToolCtx ttCtx;
    private JavaDocHelper(ThymeleafToolCtx ttCtx) {
        this.ttCtx = Objects.requireNonNull(ttCtx);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return the HTML-content of the whole right-part of top-navbar of each JavaDoc-generated HTML-page
     */
    public String getNavBarRight() {
        // TODO: add link to concrete source file in GitHub repository of proper version in git-tree
        return GithubBadgeHelper.fromCtx(ttCtx).getBadgeReleaseCatalogHTML();
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return DumpUtils.dumpAsYamlTxt(this);
    }
}
