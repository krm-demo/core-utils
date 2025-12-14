package org.krmdemo.techlabs.ghapi;

import feign.Feign;
import feign.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.krmdemo.techlabs.core.utils.MergeFunction;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.ghapi.FeignCoders.feignDecoder;
import static org.krmdemo.techlabs.ghapi.FeignCoders.feignEncoder;
import static org.krmdemo.techlabs.ghapi.GithubApi.Factory.mavenPackageName;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.PARAM_NAME__MAX_PAGE_SZIE;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.PARAM_NAME__PAGE_NUM;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.PARAM_NAME__PER_PAGE;

/**
 * Implementation of {@link GithubApi} that is based on using <a href="https://github.com/OpenFeign/feign">Open-Feign</a>
 * and the results of some remote calls are cached and lazy-initialized via {@link AtomicSafeInitializer}
 * that allows to avoid multiple invocations for:<dl>
 *     <dt>{@link #getCurrentUser()}:</dt>
 *     <dd>thread-safe property of type {@link User}, which is lazy-initialized over the invocation of
 *          {@link #userClient()}{@link UserClient#getUser() .getUser()}
 *     </dd>
 *     <dt>{@link #getCurrentRepo()}:</dt>
 *     <dd>thread-safe property of type {@link Repository}, which is lazy-initialized over the invocation of
 *          {@link #repositoryClient()}{@link RepositoryClient#getRepository(String, String) .getRepository()}
 *     </dd>
 * </dl>
 *
 * @see <a href="https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/">
 *     (Spring-IO Docs) Spring Cloud OpenFeign
 * </a>
 * @see <a href="https://www.baeldung.com/spring-cloud-openfeign">
 *     (Baeldung) Introduction to Spring Cloud <code>OpenFeign</code>
 * </a>
 */
class GithubApiFeign implements GithubApi {

    public static final String ENV_VAR__GITHUB_TOKEN = "GITHUB_TOKEN";

    public static final String SYS_PROP__GITHUB_TOKEN = "github-token";

    private final AtomicSafeInitializer<User> currentUserHolder =
        AtomicSafeInitializer.<User>builder()
            .setInitializer(this::loadCurrentUser)
            .get();

    private final AtomicSafeInitializer<Repository> currentRepoHolder =
        AtomicSafeInitializer.<Repository>builder()
            .setInitializer(this::loadCurrentRepo)
            .get();

    private final AtomicSafeInitializer<Optional<Package>> currentMvnPkgHolder =
        AtomicSafeInitializer.<Optional<Package>>builder()
            .setInitializer(this::loadCurrentRepoMavenPkgOpt)
            .get();

    private final boolean parallel;
    private final String githubToken;
    private final String ownerName;
    private final String repoName;
    private final String pkgName;

    /**
     * Constructor over the instance of {@link Factory}, which appears to be
     * a holder of all necessary properties to communicate with target REST-endpoint.
     *
     * @param factory real (non-abstract) instance of {@link Factory} with all mandatory properties
     */
    protected GithubApiFeign(Factory factory) {
        this.parallel = factory.parallel;
        this.githubToken = factory.githubToken;
        this.ownerName = factory.ownerName;
        this.repoName = factory.repoName;
        if (StringUtils.isBlank(factory.mavenPackageName)) {
            this.pkgName = mavenPackageName(factory.mavenProjectGroup, factory.mavenProjectArtifact);
        } else {
            this.pkgName = factory.mavenPackageName;
        }
    }

    private Feign.Builder feignBuilder() {
        return Feign.builder()
            .options(new Request.Options(HTTP_TIMEOUT, HTTP_TIMEOUT, true))
            .encoder(feignEncoder())
            .decoder(feignDecoder())
            .requestInterceptor(requestTemplate -> {
                requestTemplate.header("Authorization", "token " + githubToken);
                requestTemplate.header("Accept", "application/vnd.github+json");
                requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
                requestTemplate.query("package_type", "maven");
            });
    }

    private <T> T targetClient(Class<T> clientClass) {
        return feignBuilder().target(clientClass, URL_GITHUB_API);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public UserClient userClient() {
        return targetClient(UserClient.class);
    }

    // the reference to this method is used as atomic-safe-initializer
    private User loadCurrentUser() {
        return userClient().getUser();
    }

    @Override
    public User getCurrentUser() {
        try {
            return currentUserHolder.get();
        } catch (ConcurrentException concEx) {
            throw new IllegalStateException(
                "current user is not available because of errors during initialization", concEx);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public RepositoryClient repositoryClient() {
        return targetClient(RepositoryClient.class);
    }

    @Override
    public NavigableMap<String, Repository> currentUserReposMap() {
        return repositoryClient().getUserRepos().stream()
            .collect(toSortedMap(
                Repository::name,
                Function.identity()
            ));
    }

    @Override
    public NavigableMap<String, Repository> ownerReposMap(String ownerName) {
        return repositoryClient().getOwnerRepos(ownerName).stream()
            .collect(toSortedMap(
                Repository::name,
                Function.identity()
            ));
    }

    private Repository loadCurrentRepo() {
        if (StringUtils.isBlank(this.repoName)) {
            throw new IllegalStateException(
                "could not get the current repository, because there's no information " +
                    "about current repository-name in this instance of " + getClass().getSimpleName());
        }
        String currentOwnerName = StringUtils.isNotBlank(this.ownerName) ?
            this.ownerName : this.getCurrentUser().login();
        return repositoryClient().getRepository(currentOwnerName, this.repoName);
    }

    @Override
    public Repository getCurrentRepo() {
        try {
            return currentRepoHolder.get();
        } catch (ConcurrentException concEx) {
            throw new IllegalStateException(
                "current repo is not available because of errors during initialization", concEx);
        }
    }

    @Override
    public Map<String, Object> currentRepoProps() {
        if (StringUtils.isBlank(this.repoName)) {
            throw new IllegalStateException(
                "could not get the properties of current repository, because there's no information " +
                    "about current repository-name in this instance of " + getClass().getSimpleName());
        }
        String currentOwnerName = StringUtils.isNotBlank(this.ownerName) ?
            this.ownerName : this.getCurrentUser().login();
        return repositoryClient().getRepoProps(currentOwnerName, this.repoName);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public PackageClient packageClient() {
        return targetClient(PackageClient.class);
    }

    @Override
    public NavigableMap<String, Package> userMavenPackagesMap() {
        return packageClient().getUserMavenPackages().stream()
            .collect(toSortedMap(
                Package::name,
                Function.identity()
            ));
    }

    @Override
    public NavigableMap<String, Package> ownerMavenPackagesMap(String ownerName) {
        return packageClient().getOwnerMavenPackages(ownerName).stream()
            .collect(toSortedMap(
                Package::name,
                Function.identity()
            ));
    }

    @Override
    public NavigableMap<String, NavigableMap<String, Package>> userRepoToMavenPackages() {
        return userMavenPackagesMap().entrySet().stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getValue().repository().name(),
                TreeMap::new,
                toSortedMap()
            ));
    }

    @Override
    public NavigableMap<String, NavigableMap<String, Package>> ownerRepoToMavenPackages(String ownerName) {
        return ownerMavenPackagesMap(ownerName).entrySet().stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getValue().repository().name(),
                TreeMap::new,
                toSortedMap()
            ));
    }

    private Optional<Package> loadCurrentRepoMavenPkgOpt() {
        if (StringUtils.isBlank(this.repoName)) {
            throw new IllegalStateException(
                "could not get the current maven-package, because there's no information " +
                    "about current repository-name in this instance of " + getClass().getSimpleName());
        }
        NavigableMap<String, NavigableMap<String, Package>> repoToMvnPkg = userRepoToMavenPackages();
        if (!repoToMvnPkg.containsKey(this.repoName)) {
            return Optional.empty();
        }
        NavigableMap<String, Package> mvnPkgMap = repoToMvnPkg.get(this.repoName);
        return Optional.ofNullable(mvnPkgMap.get(this.pkgName));
    }

    @Override
    public Optional<Package> getCurrentRepoMavenPkgOpt() {
        try {
            return currentMvnPkgHolder.get();
        } catch (ConcurrentException concEx) {
            throw new IllegalStateException(
                "current maven-package is not available because of errors during initialization", concEx);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public PkgVerClient pkgVerClient() {
        return targetClient(PkgVerClient.class);
    }

    @Override
    public int userMavenPackageVersionsCount(String packageName) {
        Map<String, Object> queryMap = linkedMap(
            nameValue(PARAM_NAME__PAGE_NUM, "1"),
            nameValue(PARAM_NAME__PER_PAGE, "1")
        );
        PagingResult<GithubApi.PkgVer> usrPkgVerResult =
            pkgVerClient().userMavenPackageVersions(
                packageName, queryMap);
        return usrPkgVerResult.lastPage();
    }

    @Override
    public Map<String, PkgVer> userMavenPackageVersionsMap(String packageName) {
        Map<String, Object> queryMap = linkedMap(
            nameValue(PARAM_NAME__PAGE_NUM, "1"),
            nameValue(PARAM_NAME__PER_PAGE, PARAM_NAME__MAX_PAGE_SZIE)
        );
        PagingResult<GithubApi.PkgVer> firstPagingResult =
            pkgVerClient().userMavenPackageVersions(
                packageName, queryMap);
        Stream<PagingResult<PkgVer>> restPagingResult =
            restPageRange(firstPagingResult)
                .mapToObj(pageNum -> {
                    queryMap.put(PARAM_NAME__PAGE_NUM, "" + pageNum);
                    return pkgVerClient().userMavenPackageVersions(packageName, queryMap);
                });
        Stream<PagingResult<PkgVer>> pagingResultAll =
            Stream.concat(Stream.of(firstPagingResult), restPagingResult);
        if (this.parallel) {
            return pagingResultAll.parallel()
                .flatMap(PagingResult::items)
                .collect(toMap(
                    PkgVer::name,
                    Function.identity(),
                    MergeFunction.OVERWRITE.op(),
                    ConcurrentHashMap::new
                ));
        } else {
            return pagingResultAll
                .flatMap(PagingResult::items)
                .collect(toLinkedMap(PkgVer::name, Function.identity()));
        }
    }

    private IntStream restPageRange(PagingResult<?> pagingResult) {
        if (pagingResult.nextPage() == null || pagingResult.lastPage() == null) {
            return IntStream.empty();
        } else {
            return IntStream.range(pagingResult.nextPage(), pagingResult.lastPage());
        }
    }

// ---------------------------------------------------------------------------------------------

    static class FactoryImpl extends GithubApi.Factory {
        @Override
        public GithubApi create() {
            if (StringUtils.isNotBlank(mavenPackageName)) {
                if (StringUtils.isNotBlank(mavenProjectGroup)) {
                    throw new IllegalArgumentException(String.format(
                        "if 'mavenPackageName' is not blank ('%s') the mavenProjectGroup should be blank, " +
                            "but it equals to '%s'",
                        mavenPackageName, mavenProjectGroup));
                }
                if (StringUtils.isNotBlank(mavenProjectArtifact)) {
                    throw new IllegalArgumentException(String.format(
                        "if 'mavenPackageName' is not blank ('%s') the mavenProjectArtifact should be blank, " +
                            "but it equals to '%s'",
                        mavenPackageName, mavenProjectArtifact));
                }
            }
            // ??? TODO: maybe it's a good idea to take the value of token from sys-props and env-vars here
            return new GithubApiFeign(this);
        }
    }
}
