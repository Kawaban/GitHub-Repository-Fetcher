package org.repositoryservice.domain;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.repositoryservice.dto.BranchResultDto;
import org.repositoryservice.dto.GitHubApiBranchDto;
import org.repositoryservice.dto.GitHubApiRepositoryDto;
import org.repositoryservice.dto.RepositoryResultDto;

@ApplicationScoped
public class RepositoryService {
    @RestClient
    GitHubApiClient gitHubApiClient;

    public Uni<List<RepositoryResultDto>> getRepositories(String user) {
        val uniRepos = gitHubApiClient.getRepositories(user);

        val uniListOfUniResult = uniRepos.onItem().transform((List<GitHubApiRepositoryDto> repositories) -> {

            List<Uni<RepositoryResultDto>> listOfUniResult = new ArrayList<>();
            for (GitHubApiRepositoryDto repo : repositories) {
                if (!repo.isFork()) {
                    val uniBranches = gitHubApiClient.getBranches(user, repo.getName());
                    listOfUniResult.add(uniBranches
                            .onItem()
                            .transform((List<GitHubApiBranchDto> branches) -> new RepositoryResultDto(
                                    repo.getName(),
                                    repo.getOwnerLogin(),
                                    branches.stream()
                                            .map(b -> new BranchResultDto(b.getName(), b.getLastCommitSha()))
                                            .toList())));
                }
            }

            return listOfUniResult;
        });

        return uniListOfUniResult.onItem().transformToUni(listOfUniResult -> Uni.combine()
                .all()
                .unis(listOfUniResult)
                .with((listOfObjects) -> {
                    List<RepositoryResultDto> listOfResult = new ArrayList<>();
                    for (Object repo : listOfObjects) {
                        listOfResult.add((RepositoryResultDto) repo);
                    }
                    return listOfResult;
                }));
    }
}
