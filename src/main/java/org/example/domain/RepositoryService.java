package org.example.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.example.dto.BranchResultDto;
import org.example.dto.GitHubApiBranchDto;
import org.example.dto.GitHubApiRepositoryDto;
import org.example.dto.RepositoryResultDto;

@ApplicationScoped
public class RepositoryService {
    @RestClient
    GitHubApiClient gitHubApiClient;

    public Uni<List<RepositoryResultDto>> getRepositories(String user) {
        val uniRepos = gitHubApiClient.getRepositories(user);

        val uniReposAndBranches = uniRepos.onItem().transform((List<GitHubApiRepositoryDto> repositories) -> {
            List<Uni<RepositoryResultDto>> reposAndBranches = new ArrayList<>();
            for (GitHubApiRepositoryDto repo : repositories) {
                if (!repo.isFork()) {
                    val uniBranches = gitHubApiClient.getBranches(user, repo.getName());
                    reposAndBranches.add(uniBranches
                            .onItem()
                            .transform((List<GitHubApiBranchDto> branches) -> new RepositoryResultDto(
                                    repo.getName(),
                                    repo.getOwnerLogin(),
                                    branches.stream()
                                            .map(b -> new BranchResultDto(b.getName(), b.getLastCommitSha()))
                                            .toList())));
                }
            }
            return reposAndBranches;
        });

        return uniReposAndBranches.onItem().transformToUni(reposAndBranches -> Uni.combine()
                .all()
                .unis(reposAndBranches)
                .with((repos) -> {
                    List<RepositoryResultDto> result = new ArrayList<>();
                    for (Object repo : repos) {
                        result.add((RepositoryResultDto) repo);
                    }
                    return result;
                }));
    }
}
