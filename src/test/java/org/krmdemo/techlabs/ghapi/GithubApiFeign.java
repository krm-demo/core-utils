package org.krmdemo.techlabs.ghapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;

import java.time.Duration;
import java.util.List;

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
        return feignBuilder().target(UserClient.class, URL_GITHUB_API);
    }

    @Override
    public User currentUser() {
        return userClient().getUser();
    }

    static class FactoryImpl extends GithubApi.Factory {
        @Override
        public GithubApi create() {
            // ??? TODO: maybe it's a good idea to take the value of token from sys-props and env-vars here
            return new GithubApiFeign(githubToken, ownerName, repoName);
        }
    }
}
