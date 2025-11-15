package org.krmdemo.techlabs.ghapi;

import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.function.Supplier;

public interface GithubPackagesClient {

    record Package(String id, String name) {}

    @RequestLine("GET /users/{gh-user}/packages")
    List<Package> loadAll(@Param("gh-user") String user);

    enum Factory implements Supplier<GithubPackagesClient> {
        FEIGN {
            @Override
            public GithubPackagesClient get() {
                return null;
            }
        },
        HTTP_CLIENT {
            private static GithubPackagesClient httpClient = new GithubPackagesClient() {
                @Override
                public List<Package> loadAll(String user) {
                    return List.of();
                }
            };
            @Override
            public GithubPackagesClient get() {
                return httpClient;
            }
        },
        HTTP_URL_CONN;

        @Override
        public GithubPackagesClient get() {
            throw new UnsupportedOperationException(String.format(
                "%s is not supported yet", name()));
        }
    }
}
