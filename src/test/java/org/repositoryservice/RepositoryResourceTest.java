package org.repositoryservice;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.repositoryservice.domain.GitHubApiClient;
import org.repositoryservice.dto.GitHubApiBranchDto;
import org.repositoryservice.dto.GitHubApiRepositoryDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
class RepositoryResourceTest {

    @InjectMock
    @RestClient
    GitHubApiClient gitHubApiClient;

    @Test
    void testGetNonForkRepositoriesByUser() {

        String user = "testuser";

        // repo with multiple branches
        GitHubApiRepositoryDto repo1 = new GitHubApiRepositoryDto("repo1", "testuser", false);
        // repo with single branch
        GitHubApiRepositoryDto repo2 = new GitHubApiRepositoryDto("repo2", "testuser", false);
        // repo with branches but forked
        GitHubApiRepositoryDto repo3 = new GitHubApiRepositoryDto("repo3", "testuser", true);
        // repo with no branches
        GitHubApiRepositoryDto repo4 = new GitHubApiRepositoryDto("repo4", "testuser", false);
        // repo with no branches and forked
        GitHubApiRepositoryDto repo5 = new GitHubApiRepositoryDto("repo5", "testuser", true);

        GitHubApiBranchDto branch1 = new GitHubApiBranchDto( "commit-sha-123","main");
        GitHubApiBranchDto branch2 = new GitHubApiBranchDto( "commit-sha-456","dev");
        GitHubApiBranchDto branch3 = new GitHubApiBranchDto( "commit-sha-883","feat/some-feature");
        GitHubApiBranchDto branch4 = new GitHubApiBranchDto( "commit-sha-452","main");
        GitHubApiBranchDto branch5 = new GitHubApiBranchDto( "commit-sha-753","main");
        GitHubApiBranchDto branch6 = new GitHubApiBranchDto( "commit-sha-214","main");



        Mockito.when(gitHubApiClient.getRepositories(user))
                .thenReturn(Uni.createFrom().item(List.of(repo1, repo2, repo3, repo4, repo5)));

        Mockito.when(gitHubApiClient.getBranches(user, "repo1"))
                .thenReturn(Uni.createFrom().item(List.of(branch1, branch2, branch3)));

        Mockito.when(gitHubApiClient.getBranches(user, "repo2"))
                .thenReturn(Uni.createFrom().item(List.of(branch4)));

        Mockito.when(gitHubApiClient.getBranches(user, "repo3"))
                .thenReturn(Uni.createFrom().item(List.of(branch5, branch6)));

        Mockito.when(gitHubApiClient.getBranches(user, "repo4"))
                .thenReturn(Uni.createFrom().item(List.of()));

        Mockito.when(gitHubApiClient.getBranches(user, "repo5"))
                .thenReturn(Uni.createFrom().item(List.of()));


        given()
                .when().get("/api/repos/" + user)
                .then()
                .statusCode(200)
                .body("$.size()", is(3)) // Only non-forked repos should be returned (repo1, repo2, repo4)


                .body("[0].name", is("repo1"))
                .body("[0].owner", is(user))
                .body("[0].branches.size()", is(3))
                .body("[0].branches[0].name", is("main"))
                .body("[0].branches[0].lastCommitSha", is("commit-sha-123"))
                .body("[0].branches[1].name", is("dev"))
                .body("[0].branches[1].lastCommitSha", is("commit-sha-456"))
                .body("[0].branches[2].name", is("feat/some-feature"))
                .body("[0].branches[2].lastCommitSha", is("commit-sha-883"))


                .body("[1].name", is("repo2"))
                .body("[1].owner", is(user))
                .body("[1].branches.size()", is(1))
                .body("[1].branches[0].name", is("main"))
                .body("[1].branches[0].lastCommitSha", is("commit-sha-452"))


                .body("[2].name", is("repo4"))
                .body("[2].owner", is(user))
                .body("[2].branches.size()", is(0));
    }
}
