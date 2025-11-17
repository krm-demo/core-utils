package org.krmdemo.techlabs.ghapi;

import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;

import java.time.Duration;

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

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
            .decoder(new JacksonDecoder())
            .requestInterceptor(requestTemplate -> {
                requestTemplate.header("Authorization", "token " + githubToken);
                requestTemplate.header("Accept", "application/vnd.github+json");
                requestTemplate.header("X-GitHub-Api-Version", "2022-11-28");
            });
    }

    @Override
    public UserClient userClient() {
        return feignBuilder().target(UserClient.class, URL_GITHUB_API);
    }

    @Override
    public User currentUser() {
        return userClient().get();
    }

    static class FactoryImpl extends GithubApi.Factory {
        @Override
        public GithubApi create() {
            // ???
            return new GithubApiFeign(githubToken, ownerName, repoName);
        }
    }
}
