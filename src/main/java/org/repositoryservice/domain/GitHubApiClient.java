package org.repositoryservice.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.repositoryservice.dto.GitHubApiBranchDto;
import org.repositoryservice.dto.GitHubApiRepositoryDto;

@ApplicationScoped
@RegisterRestClient(configKey = "github-api")
public interface GitHubApiClient {
    @Path("/users/{user}/repos")
    @GET
    Uni<List<GitHubApiRepositoryDto>> getRepositories(@PathParam("user") String user);

    @Path("/repos/{user}/{repository}/branches")
    @GET
    Uni<List<GitHubApiBranchDto>> getBranches(
            @PathParam("user") String user, @PathParam("repository") String repository);
}
