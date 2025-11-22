package org.krmdemo.techlabs.ghapi;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.Function;

import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;

/**
 * Implementation of {@link GithubApi} that is based on using <a href="https://github.com/OpenFeign/feign">Open-Feign</a>
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


    private final String githubToken;
    private final String ownerName;
    private final String repoName;

    private GithubApiFeign(String githubToken, String ownerName, String repoName) {
        this.githubToken = githubToken;
        this.ownerName = ownerName;
        this.repoName = repoName;
    }

    private Feign.Builder feignBuilder() {
        return Feign.builder()
            .options(new Request.Options(HTTP_TIMEOUT, HTTP_TIMEOUT, true))
            .decoder(jacksonDecoder)
            .encoder(jacksonEncoder)
            .requestInterceptor(requestTemplate -> {
                requestTemplate.header("Authorization", "token " + githubToken);
                requestTemplate.header("Accept", "application/vnd.github+json");
                requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
            });
    }

    private static final JacksonDecoder jacksonDecoder =
        new JacksonDecoder(List.of(CoreDateTimeUtils.jacksonModuleDTT()));

    private static final JacksonEncoder jacksonEncoder =
        new JacksonEncoder(List.of(CoreDateTimeUtils.jacksonModuleDTT()));

    @Override
    public UserClient userClient() {
        return targetClient(UserClient.class);
    }

    @Override
    public User currentUser() {
        return userClient().getUser();
    }

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

    @Override
    public Repository currentRepo() {
        if (StringUtils.isBlank(this.repoName)) {
            throw new IllegalStateException(
                "could not get the current repository, because there's no information " +
                    "about current repository-name in this instance of " + getClass().getSimpleName());
        }
        String currentOwnerName = StringUtils.isNotBlank(this.ownerName) ?
            this.ownerName : this.currentUser().login();
        return repositoryClient().getRepository(currentOwnerName, this.repoName);
    }

    @Override
    public Map<String, Object> currentRepoProps() {
        if (StringUtils.isBlank(this.repoName)) {
            throw new IllegalStateException(
                "could not get the properties of current repository, because there's no information " +
                    "about current repository-name in this instance of " + getClass().getSimpleName());
        }
        String currentOwnerName = StringUtils.isNotBlank(this.ownerName) ?
            this.ownerName : this.currentUser().login();
        return repositoryClient().getRepoProps(currentOwnerName, this.repoName);
    }

    private <T> T targetClient(Class<T> clientClass) {
        return feignBuilder().target(clientClass, URL_GITHUB_API);
    }

    static class FactoryImpl extends GithubApi.Factory {
        @Override
        public GithubApi create() {
            // ??? TODO: maybe it's a good idea to take the value of token from sys-props and env-vars here
            return new GithubApiFeign(githubToken, ownerName, repoName);
        }
    }
}
