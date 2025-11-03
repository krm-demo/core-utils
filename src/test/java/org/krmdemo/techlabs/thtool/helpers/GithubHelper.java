package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static org.krmdemo.techlabs.core.utils.CorePropsUtils.propValueStr;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to work with miscellaneous properties of Git-Hub.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code gh}.
 */
public class GithubHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link GithubHelper}
     */
    public static final String VAR_NAME__HELPER = "gh";

    /**
     * The name of <b>{@code th-tool}</b>-variable, which is loaded from {@code var-github.json}-file
     * that becomes available in GitHub-workflows via following instruction in {@code run}-step:
     * {@snippet : echo '${{ toJson(github) }}' > .github/th-vars/var-github.json }
     */
    public static final String VAR_NAME__GITHUB = "github";

    public static GithubHelper fromCtxLazy() {
        return fromCtxLazy(new ThymeleafToolCtx());
    }

    public static GithubHelper fromCtxLazy(ThymeleafToolCtx ttCtx) {
        GithubHelper helper = fromCtx(ttCtx);
        if (helper == null) {
            register(ttCtx);
            helper = fromCtx(ttCtx);
        }
        return helper;
    }

    /**
     * A factory-method that returns an instance of {@link GithubHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link GithubHelper} for access from other helpers
     */
    public static GithubHelper fromCtx(ThymeleafToolCtx ttCtx) {
        return ttCtx.typedVar(VAR_NAME__HELPER, GithubHelper.class);
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependencies between <b>{@code th-tool}</b> and helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        GithubHelper helper = new GithubHelper(ttCtx);
        ttCtx.setVariable(VAR_NAME__HELPER, helper);
    }

    private final ThymeleafToolCtx ttCtx;
    private GithubHelper(ThymeleafToolCtx ttCtx) {
        this.ttCtx = Objects.requireNonNull(ttCtx);
    }

    /**
     * @return the main underlying <b>{@code th-tool}</b>-variable as properties-map (similar to initial JSON-Object)
     */
    @JsonProperty(VAR_NAME__GITHUB)
    public Map<String, Object> propsGithub() {
        return this.ttCtx.propsVar(VAR_NAME__GITHUB);
    }

    // --------------------------------------------------------------------------------------------

    final static String VAR_PROP_NAME__WORKFLOW = "workflow";

    public String getWorkflowName() {
        return propValueStr(propsGithub(), VAR_PROP_NAME__WORKFLOW);
    }

    /**
     * Getting the name of current GitHub-Repository  {@code "core-utils"},
     * which is exactly the same as {@link MavenHelper#getProjectArtifact() maven artifact-name}.
     *
     * @return the same as {@code ${github.event.repository.name}} in <b>{@code th-tool}</b> expression
     */
    public String getRepoName() {
        return propValueStr(propsGithub(), "event", "repository", "name");
    }

    /**
     * @return the same as {@code ${github.event.repository.html_url}} in <b>{@code th-tool}</b> expression
     */
    public String getProjectRepoHtmlUrl() {
        return propValueStr(propsGithub(), "event", "repository", "html_url");
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }
}
