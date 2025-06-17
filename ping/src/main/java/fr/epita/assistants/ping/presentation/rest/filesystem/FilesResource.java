package fr.epita.assistants.ping.presentation.rest.filesystem;

import fr.epita.assistants.ping.api.request.MoveFileRequest;
import fr.epita.assistants.ping.api.request.RelativePathRequest;
import fr.epita.assistants.ping.errors.Exceptions.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import fr.epita.assistants.ping.domain.service.FileService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.identity.SecurityIdentity;

import static fr.epita.assistants.ping.utils.Logger.*;


@Path("/api/projects/{projectId}")
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
        logInfo("The user request file at " + path);
        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");

            byte[] data = fileService.file_data(projectId, path, userId, isAdmin);
            logSuccess("The operation was successful");
            return Response.ok(data, MediaType.APPLICATION_OCTET_STREAM_TYPE).build();
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project or the relative path could not be found")).build();
        }
        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (PathException | IOException e) { // 400
            logError("Error 400: The relative path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The relative path is invalid (null or empty for example)")).build();
        }
    }


    @DELETE
    @Path("/files")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /* Delete a file from the file system and all its content, be careful if the file is the root
        you should only empty it and not remove the root file of the project.

        Any member of the project or an admin can access this endpoint.
    */
    public Response deleteFiles(@PathParam("projectId") UUID projectId, RelativePathRequest request) {
        logInfo("The user request to delete a file at " + request.relativePath);

        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            fileService.deleteFile(projectId, userId, request.relativePath, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The file was deleted")).build(); // 204
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project or the file could not be found")).build();
        }
        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (PathException e) { // 400
            logError("Error 400: The relative path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The source or destination path is invalid (null or empty for example)")).build();
        }
    }


    @POST
    @Path("/files")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /*
       Create a new file on the file system.

        Any member of the project or an admin can access this endpoint.
     */
    public Response postFiles(@PathParam("projectId") UUID projectId, RelativePathRequest request) {
        logInfo("The user request to create a file at " + request.relativePath);

        try {

            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            fileService.createFile(projectId, userId, request.relativePath, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.CREATED).entity(new ErrorInfo("The file was created")).build(); // 201
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");

            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project could not be found")).build();
        }
        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (PathException | IOException e) { // 400
            logError("Error 400: The relative path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The source or destination path is invalid (null or empty for example)")).build();
        }
        catch (AlreadyExistException e) // 409
        {
            logError("Error 409: The file already exists");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The file already exists")).build();
        }
    }


    @PUT
    @Path("/files/move")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /*
     Move a file to a new location or rename it if the destination is in the same parent directory

        Any member of the project or an admin can access this endpoint.
     */
    public Response putFilesMove(@PathParam("projectId") UUID projectId,
                                 MoveFileRequest request) {
        logInfo("The user request to move a file from " + request.src + " to " + request.dst);

        try {

            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            fileService.moveFile(projectId, userId, request.src,request.dst, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The file was renamed")).build(); // 204
        }
        catch (PathException | IOException e) { // 400
            logError("Error 400: The relative path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The source or destination path is invalid (null or empty for example)")).build();
        }

        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");

            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project could not be found")).build();
        }
        catch (AlreadyExistException e) // 409
        {
            logError("Error 409: The file already exists");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The file already exists")).build();
        }
    }


    @POST
    @Path("/files/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed({"user", "admin"})
    /*
        Upload data to create or modify a file, it should write the content
        of the body to the file and create it if it does not exist.

        The content type received must be application/octet-stream.

        You can retrieve the data of the body using the InputStream type for your parameter.

        Any member of the project or an admin can access this endpoint.
     */
    public Response postFilesUpload(@PathParam("projectId") UUID projectId,
                                    @QueryParam("path") String path,
                                    InputStream inputStream) {
        logInfo("The user request to upload a file at " + path);
        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            fileService.uploadFile(projectId, userId, path, inputStream, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.CREATED).entity(new ErrorInfo("The file was created")).build(); // 201
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");

            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project could not be found")).build();
        }
        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }

        catch (PathException | IOException e) { // 400
            logError("Error 400: The relative path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The relative path is invalid (null or empty for example)")).build();
        }
    }
}