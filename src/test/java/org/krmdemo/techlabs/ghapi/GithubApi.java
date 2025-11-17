package org.krmdemo.techlabs.ghapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.RequestLine;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;

import java.time.Duration;
import java.util.Map;

public interface GithubApi {

    String URL_GITHUB_API = "https://api.github.com";

    Duration HTTP_TIMEOUT = Duration.ofSeconds(5);

    record User(
        String id,
        String login,
        @JsonProperty("created_at") DateTimeTriplet createdAt,
        @JsonProperty("updated_at") DateTimeTriplet updatedAt
    ) {}

    interface UserClient {
        @RequestLine("GET /user")
        User getUser();

        @RequestLine("GET /user")
        Map<String, Object> getUserProps();
    }

    UserClient userClient();

    User currentUser();

    static Factory feignFactory() {
        return new GithubApiFeign.FactoryImpl();
    }

    abstract class Factory {
        String githubToken;
        String ownerName;
        String repoName;

        public abstract GithubApi create();

        public Factory githubToken(String githubToken) {
            this.githubToken = githubToken;
            return this;
        }

        public Factory ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Factory repoName(String repoName) {
            this.repoName = ownerName;
            return this;
        }
    }
}
