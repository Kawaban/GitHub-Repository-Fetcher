package org.example.domain;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.example.dto.ErrorDto;
import org.example.dto.RepositoryResultDto;

@Path("/api/repos")
public class RepositoryResource {

    private final RepositoryService repositoryService;

    RepositoryResource(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GET
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getRepositoriesByUser(@PathParam("user") String user) {
        Uni<List<RepositoryResultDto>> uniResult = repositoryService.getRepositories(user);
        return uniResult
                .onItem()
                .transform(result -> Response.ok(result).build())
                .onFailure()
                .recoverWithItem(this::handleFailure);
    }

    private Response handleFailure(Throwable f) {
        if (f.getMessage().contains("404")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorDto("User doesn't exists", 404))
                    .build();
        }
        return Response.serverError()
                .entity(new ErrorDto("Internal server error", 500))
                .build();
    }
}
