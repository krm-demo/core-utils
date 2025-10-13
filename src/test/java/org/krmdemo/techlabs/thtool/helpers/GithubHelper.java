package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.util.Map;
import java.util.Objects;

import static org.krmdemo.techlabs.thtool.ThToolCtxUtils.propValueStr;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to work with miscellaneous properties of Git-Hub.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code gh}.
 */
public class GithubHelper {

    public static final String VAR_NAME__HELPER = "gh";
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

    public static GithubHelper fromCtx(ThymeleafToolCtx ttCtx) {
        return ttCtx.typedVar(VAR_NAME__HELPER, GithubHelper.class);
    }

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
