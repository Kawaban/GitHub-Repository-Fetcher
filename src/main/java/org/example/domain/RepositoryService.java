package org.example.domain;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
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
            List<Uni<Tuple2<List<GitHubApiBranchDto>, GitHubApiRepositoryDto>>> reposAndBranches = new ArrayList<>();

            for (GitHubApiRepositoryDto repo : repositories) {
                if (!repo.isFork()) {
                    val uniBranches = gitHubApiClient.getBranches(user, repo.getName());
                    reposAndBranches.add(Uni.combine()
                            .all()
                            .unis(uniBranches, Uni.createFrom().item(repo))
                            .asTuple());
                }
            }
            return reposAndBranches;
        });

        return uniReposAndBranches
                .onItem()
                .transformToUni(
                        (List<Uni<Tuple2<List<GitHubApiBranchDto>, GitHubApiRepositoryDto>>> reposAndBranches) ->
                                Uni.combine().all().unis(reposAndBranches).with((tuples) -> {
                                    List<RepositoryResultDto> repositoryResultDtoList = new ArrayList<>();

                                    for (Object tuple : tuples) {
                                        Tuple2<List<GitHubApiBranchDto>, GitHubApiRepositoryDto> repoAndBranch =
                                                (Tuple2<List<GitHubApiBranchDto>, GitHubApiRepositoryDto>) tuple;

                                        List<BranchResultDto> branchData = new ArrayList<>();
                                        for (GitHubApiBranchDto b : repoAndBranch.getItem1()) {
                                            branchData.add(new BranchResultDto(b.getName(), b.getLastCommitSha()));
                                        }

                                        repositoryResultDtoList.add(new RepositoryResultDto(
                                                repoAndBranch.getItem2().getName(),
                                                repoAndBranch.getItem2().getOwnerLogin(),
                                                branchData));
                                    }
                                    return repositoryResultDtoList;
                                }));
    }
}
