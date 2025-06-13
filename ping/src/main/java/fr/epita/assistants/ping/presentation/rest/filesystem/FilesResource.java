package fr.epita.assistants.ping.presentation.rest.filesystem;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.UUID;

import fr.epita.assistants.ping.domain.executor.FileService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.NotAuthorizedException;
import fr.epita.assistants.ping.errors.Exceptions.PathException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import io.quarkus.security.identity.SecurityIdentity;



@Path("/api/projects/{projectId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FilesResource {


    @Inject public SecurityIdentity identity;
    @Inject 
    FileService fileService;

    @GET
    @Path("/files")
    @RolesAllowed({"user", "admin"})
    /* Retrieve the content of a file.

        It should return a byte array of the content of the file.

        The content type must be application/octet-stream.

        Any member of the project or an admin can access this endpoint.
    */
    public Response getFiles(@PathParam("projectId") UUID projectId,
                             @QueryParam("path") String path) {
        //FIXME: LOGGER
        try {
            //FIXME: LOGGER
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");

            byte[] data = fileService.file_data(projectId, path, userId, isAdmin);
            return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM_TYPE).build();
        }
        catch (InvalidException e) { // 404
            //FIXME: LOGGER
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project or the relative path could not be found")).build();
        }
        catch (UserException e) { // 403
            //FIXME: LOGGER

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (NotAuthorizedException e) { // 401
            //FIXME: LOGGER

            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not Authorized")).build();
        }
        catch (PathException | IOException e) { // 400
            //FIXME: LOGGER

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The relative path is invalid (null or empty for example)")).build();
        }
    }


    @DELETE
    @Path("/files")

    /* Delete a file from the file system and all its content, be careful if the file is the root
        you should only empty it and not remove the root file of the project.

        Any member of the project or an admin can access this endpoint.
    */
    public Response deleteFiles(@PathParam("projectId") UUID projectId) {
        //FIXME: LOGGER
        return Response.ok("Hello World !").build();
    }


    @POST
    @Path("/files")
    /*
       Create a new file on the file system.

        Any member of the project or an admin can access this endpoint.
     */
    public Response postFiles(@PathParam("projectId") UUID projectId,
    @QueryParam("path") String path) {
        // LOGGER
        return Response.ok("Hello World !").build();
    }

    @PUT
    @Path("/files/move")
    /*
     Move a file to a new location or rename it if the destination is in the same parent directory

        Any member of the project or an admin can access this endpoint.
     */
    public Response putFilesMove(@PathParam("projectId") UUID projectId,
    @QueryParam("path") String path) {
        // LOGGER
        return Response.ok("Hello World !").build();
    }


    @POST
    @Path("/files/upload")
    /*
        Upload data to create or modify a file, it should write the content
        of the body to the file and create it if it does not exist.

        The content type received must be application/octet-stream.

        You can retrieve the data of the body using the InputStream type for your parameter.

        Any member of the project or an admin can access this endpoint.
     */
    public Response postFilesUpload(@PathParam("projectId") UUID projectId,
    @QueryParam("path") String path) {
        // LOGGER
        return Response.ok("Hello World !").build();
    }
}