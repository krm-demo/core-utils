package org.krmdemo.techlabs.thtool.helpers;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsJsonTxt;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileLines;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.pathParts;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.pathPartsStr;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.multiLine;

/**
 * This class represents a <b>{@code th-tool}</b>-helper to be invoked from HTML-pages,
 * which are rendered by standard JDK's <b>{@code javadoc}</b>-tool.
 * <hr/>
 * In this particular project that JDK's tool is invoked using
 * <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/">
 *     Apache Maven Javadoc Plugin
 * </a> and the most important feature that we are using here - is ability to
 * provide custom HTML-fragment for the right-part of top-navigation bar, which is performed
 * in <a href="https://github.com/krm-demo/core-utils/blob/main/pom.xml#L536-L537">{@code 'pom.xml'}</a>
 *
 * @see <a href="https://docs.oracle.com/en/java/javase/21/javadoc/javadoc-tool.html">
 *     (JDK 21) JavaDoc Guide
 * </a>
 * @see <a href="https://docs.oracle.com/en/java/javase/21/docs/specs/man/javadoc.html#:~:text=header,mypackage">
 *     The <code>javadoc</code> Command
 * </a>
 */
@Slf4j
@JsonPropertyOrder(alphabetic = true)
public class JavaDocHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link JavaDocHelper}
     */
    public static final String VAR_NAME__HELPER = "jdh";

    /**
     * The root-directory of Java <b>main-source</b>
     * (unit-tests are in separate directory, that is called <b>test-source</b>),
     * which corresponds to <i>maven-</i> and <i>gradle-</i> default project-structure.
     */
    public static final Path LOCAL_PATH__SRC_MAIN_JAVA = Path.of("src/main/java");

    /**
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link JavaDocHelper} for unit-tests
     */
    public static JavaDocHelper fromCtxLazy(ThymeleafToolCtx ttCtx) {
        JavaDocHelper helper = fromCtx(ttCtx);
        if (helper == null) {
            JavaDocHelper.register(ttCtx);
            GithubBadgeHelper.register(ttCtx);
            GithubHelper.register(ttCtx);
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
     * which allows to decouple the dependencies between <b>{@code th-tool}</b> and helper-objects.
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

    /**
     * Lazy-initialized correspondence between JavaDoc-report files and GitHub-project source-files.
     * The {@link Map.Entry#getKey() key} is a path to the parent directory of the JavaDoc-generated file,
     * and the {@link Map.Entry#getValue() value} is path to corresponding directory in main-source
     * (the root content is expected to be {@code ./src/main/java} in the <b>{@code git}</b>-repo checkout)
     */
    protected Map<Path, Path> packagePathMap = null;

    // --------------------------------------------------------------------------------------------

    /**
     * This HTML-fragment is inserted as a title of main JavaDoc-generated 'Overview' HTML-page
     * via following XML-tag in {@code pom.xml}-file of this project:{@snippet : . . . . . . . . .
     * <plugin>
     *   <groupId>org.apache.maven.plugins</groupId>
     *   <artifactId>maven-javadoc-plugin</artifactId>
     *   <version>${maven.javadoc.plugin.version}</version>
     *   <configuration>
     *     . . . . . . . . .
     *     <doctitle>[(${ jdh.overviewDocTitle })]</doctitle>
     *     . . . . . . . . .
     *   </configuration>
     * </plugin>
     * . . . . . . . . .}
     *
     * @return the content of title at JavaDoc-generated 'Overview' HTML-page
     */
    public String getOverviewDocTitle() {
        return String.format("""
            Java-API Documentation for <code>%s</code>-library of version <a href="%s">%s</a>""",
            MavenHelper.fromCtx(ttCtx).getProjectArtifact(),
            GithubBadgeHelper.fromCtx(ttCtx).getGitHubCurrentRootUrl(),
            MavenHelper.fromCtx(ttCtx).getCurrentProjectVersion()
        );
    }

    /**
     * This HTML-fragment is inserted to each JavaDoc-generated HTML-page
     * via following XML-tag in {@code pom.xml}-file of this project:{@snippet : . . . . . . . . .
     * <plugin>
     *   <groupId>org.apache.maven.plugins</groupId>
     *   <artifactId>maven-javadoc-plugin</artifactId>
     *   <version>${maven.javadoc.plugin.version}</version>
     *   <configuration>
     *     . . . . . . . . .
     *     <header>[(${ jdh.navBarRight })]</header>
     *     . . . . . . . . .
     *   </configuration>
     * </plugin>
     * . . . . . . . . .}
     *
     * @return the HTML-content of the whole right-part of top-navbar of each JavaDoc-generated HTML-page,
     * which includes:<ul>
     *     <li>badge to {@link GithubBadgeHelper#getBadgeReleaseCatalogHTML() 'Release Catalog'}</li>
     *     <li>badge to {@link #getBadgeGitHubHTML() 'GitHub  project source'}</li>
     * </ul>
     */
    public String getNavBarRight() {
        logInfo("%n%n==== %s.getNavBarRight(): =====%ntth --> %s",
            getClass().getSimpleName(), ttCtx.getThToolHelper());
        logInfo("- packagePathSet().size() = %d;", packagePathMap().size());
        logInfo("- githubSourceSuffix() = '%s';", getGitHubSourcePath());

        String navBarRightHtml = String.format("""
            <div id="div-release-catalog-badge" class="nav-bar-right-first">
            %s
            </div>
            <div id="div-github-source-badge" class="nav-bar-right-second">
            %s
            </div>""",
            GithubBadgeHelper.fromCtx(ttCtx).getBadgeReleaseCatalogHTML(),
            getBadgeGitHubHTML()
        );
        logInfo("------------ navBarRightHtml: ------------");
        logInfo(navBarRightHtml);
        logInfo("==========================================");
        return navBarRightHtml;
    }

    /**
     * @return the HTML-badge to 'GitHub project source' (to be inserted at each HTML-page in processed JavaDoc-report)
     */
    public String getBadgeGitHubHTML() {
        String sourceSuffix = getGitHubSourcePath();
        return String.format("""
            <a href="%s"
               class="github-project-source-badge-link">
              <img alt="a badge to GitHub-project %s for version '%s'"
                   title="go to GitHub-project %s for version '%s'"
                   class="github-project-source-badge"
                   src="%s" />
            </a>""",
            GithubBadgeHelper.fromCtx(ttCtx).githubSourceUrl(sourceSuffix),
            StringUtils.isBlank(sourceSuffix) ? "" : String.format("source '%s'", sourceSuffix),
            MavenHelper.fromCtx(ttCtx).getCurrentProjectVersion(),
            StringUtils.isBlank(sourceSuffix) ? "" : String.format("source '%s'", sourceSuffix),
            MavenHelper.fromCtx(ttCtx).getCurrentProjectVersion(),
            GithubBadgeHelper.fromCtx(ttCtx).badgeUrlGitHub());
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Getting the path-part of URL to GitHub-source file, which corresponds
     * to the JavaDoc-file that is currently processed by <b>{@code th-tool}</b>.
     * The returning path corresponds to the local-path
     * of the same file in the local <b>{@code git}</b>-repo checkout
     *
     * @return the path-part of URL to any GitHub-source file as {@link String}
     */
    public String getGitHubSourcePath() {
        File javaDocFile = ttCtx.getThToolHelper().getInputFile();
        logInfo("- detecting the path-suffix to GitHub source for %s;", javaDocFile);
        if (javaDocFile == null || !javaDocFile.isFile()) {
            logInfo("- (input file is invalid - returning empty suffix");
            return "";
        }

        Path javaDocParentPath = javaDocFile.getParentFile().toPath();
        if (javaDocParentPath.endsWith("class-use")) {
            logInfo("- '.../class-use' was truncated;");
            javaDocParentPath = javaDocParentPath.getParent();
        }
        logInfo("- javaDocParentPath is %s;", pathParts(javaDocParentPath));
        if (!packagePathMap().containsKey(javaDocParentPath)) {
            logInfo("- No! the file is not from proper JavaDoc-path");
            return "";
        }

        Path javaSourceDir = packagePathMap.get(javaDocParentPath);
        File javaSourceFile = javaSourceDir.resolve(sourceFileName(javaDocFile)).toFile();
        logInfo("- going to detect java-source file '%s' as a target link", javaSourceDir);
        if (javaSourceFile.isFile()) {
            logInfo("- '%s' is existing java-source file !!!", javaSourceDir);
            return pathPartsStr(javaSourceFile.toPath());
        } else {
            logInfo("- '%s' does not exist - returning the parent directory '%s'.",
                javaSourceFile, javaSourceDir);
            return pathPartsStr(javaSourceDir);
        }
    }

    /**
     * @param javaDocFile a file that was generated as a part of JavaDoc-report
     * @return the file-name that corresponds to Java-source file-name (no parent path),
     *         which should point to standard {@code package-info.java}
     *         if the inout file-name starts with {@code "package-"}
     */
    private String sourceFileName(File javaDocFile) {
        String javaDocFileName = javaDocFile.getName();
        int firstDot = javaDocFileName.indexOf('.');
        String javaDocFileSlug = javaDocFileName.substring(0, firstDot);
        if (javaDocFileSlug.startsWith("package-")) {
            return "package-info.java";
        } else {
            return javaDocFileSlug + ".java";
        }
    }

    /**
     * @return lazy-loading the list of Java-packages from predefined file in JavaDoc-root directory
     */
    private synchronized Map<Path, Path> packagePathMap() {
        // TODO: think about dedicated th-tool command for JavaDoc to avoid synchronization
        if (packagePathMap != null) {
            return packagePathMap;
        }
        File javaDocRoot = ttCtx.getThToolHelper().getInputDir();
        if (javaDocRoot == null || !javaDocRoot.isDirectory()) {
            throw new IllegalStateException(
                "could not detect the JavaDoc-root directory, because 'inputDir' invalid - " + javaDocRoot);
        }
        Path packagesPath = javaDocRoot.toPath().resolve("packages");
        logInfo("- loading the list of JavaDoc packages from '%s':", packagesPath);
        List<String> packagesLines = loadFileLines(packagesPath);
        logInfo(packagesLines.stream().collect(multiLine()));

        this.packagePathMap = packagesLines.stream()
            .collect(toSortedMap(
                this::packageNameToJavaDocPath,
                this::packageNameToSourceMainPath
            ));
        logInfo("- packagePathSet --> %s", dumpAsJsonTxt(this.packagePathMap));
        return this.packagePathMap;
    }

    private Path packageNameToJavaDocPath(String packageName) {
        File javaDocRoot = ttCtx.getThToolHelper().getInputDir();
        return javaDocRoot.toPath().resolve(Paths.get("", packageName.split("\\.")));
    }

    private Path packageNameToSourceMainPath(String packageName) {
        return LOCAL_PATH__SRC_MAIN_JAVA.resolve(Paths.get("", packageName.split("\\.")));
    }

    // --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return DumpUtils.dumpAsYamlTxt(this);
    }

    private static void logInfo(String formatString, Object... formatArgs) {
        if (log.isInfoEnabled()) {
            log.info(String.format(formatString, formatArgs));
        }
    }

}
