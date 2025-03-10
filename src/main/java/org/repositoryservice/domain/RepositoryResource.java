package org.repositoryservice.domain;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.repositoryservice.dto.ErrorDto;
import org.repositoryservice.dto.RepositoryResultDto;

@Path("/api/repos")
public class RepositoryResource {

    private final RepositoryService repositoryService;

    RepositoryResource(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GET
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getNonForkRepositoriesByUser(@PathParam("user") String user) {
        Uni<List<RepositoryResultDto>> uniResult = repositoryService.getRepositories(user);
        return uniResult
                .onItem()
                .transform(result -> Response.ok(result).build())
                .onFailure()
                .recoverWithItem(this::handleFailure);
    }

    private Response handleFailure(Throwable failure) {
        if (failure.getMessage().contains("Not Found, status code 404")
                && failure.getMessage().contains("getRepositories")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDto("User doesn't exists", 404))
                    .build();
        }
        return Response.serverError()
                .entity(new ErrorDto("Internal server error", 500))
                .build();
    }
}
