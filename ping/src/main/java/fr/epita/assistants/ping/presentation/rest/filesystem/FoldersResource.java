package fr.epita.assistants.ping.presentation.rest.filesystem;

import fr.epita.assistants.ping.api.request.MoveFileRequest;
import fr.epita.assistants.ping.api.request.RelativePathRequest;
import fr.epita.assistants.ping.api.response.GetFolderResponse;
import fr.epita.assistants.ping.domain.service.FolderService;
import fr.epita.assistants.ping.errors.Exceptions.AlreadyExistException;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.PathException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.UUID;

import static fr.epita.assistants.ping.utils.Logger.*;


@Path("/api/projects/{projectId}")
public class FoldersResource {


    @Inject
    private SecurityIdentity identity;
    @Inject
    FolderService folderService;

    @GET
    @Path("/folders")
    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    /* List the content of the folder located at the given path in the project with the given id. By default it lists the root folder.

        It is NOT a recursive listing, it only lists the immediate children of the folder.

        Any member of the project or an admin can access this endpoint.
    */
    public Response getFolders(@PathParam("projectId") UUID projectId,
                             @QueryParam("path") String path) {
        logInfo("The user request data of a folder at " + path);

        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            logSuccess("The operation was successful");

            GetFolderResponse[] data = folderService.folder_data(projectId, path, userId, isAdmin);
            return Response.ok(data, MediaType.APPLICATION_JSON).build(); // 200
        } catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");

            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project or the relative path could not be found")).build();
        } catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
    }


    @DELETE
    @Path("/folders")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /* Delete a file from the file system and all its content, be careful if the file is the root
        you should only empty it and not remove the root file of the project.

        Any member of the project or an admin can access this endpoint.
    */
    public Response deleteFolders(@PathParam("projectId") UUID projectId, RelativePathRequest request) {
        logInfo("The user request to delete a folder at " + request.relativePath);
        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            folderService.deleteFolder(projectId, userId, request.relativePath, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The folder was deleted")).build(); // 204
        }
        catch (InvalidException e) { // 404
            logError("Error 404: The project or the path could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project or the folder could not be found")).build();
        }
        catch (UserException e) { // 403
            logError("Error 403: The user is not allowed to access the project or a path traversal attack was detected");

            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access the project or a path traversal attack was detected")).build();
        }
        catch (PathException e) { // 400
            logError("Error 400: The source or destination path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The source or destination path is invalid (null or empty for example)")).build();
        }
    }


    @POST
    @Path("/folders")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /*
       Create a new file on the file system.

        Any member of the project or an admin can access this endpoint.
     */
    public Response postFolders(@PathParam("projectId") UUID projectId, RelativePathRequest request) {
        logInfo("The user request to create a folder at " + request.relativePath);
        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            folderService.createFolder(projectId, userId, request.relativePath, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.CREATED).entity(new ErrorInfo("The folder was created")).build(); // 201
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
            logError("Error 400: The source or destination path is invalid (null or empty for example)");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The source or destination path is invalid (null or empty for example)")).build();
        }
        catch (AlreadyExistException e) // 409
        {
            logError("Error 409: The folder already exists");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The folder already exists")).build();
        }
    }
    @PUT
    @Path("/folders/move")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    /*
     Move a folder to a new location, or rename it if the destination is in the same parent directory.
        Any member of the project or an admin can access this endpoint.
     */
    public Response putFolderMove(@PathParam("projectId") UUID projectId,
                                 MoveFileRequest request) {
        logInfo("The user request to move a folder from " + request.src + " to " + request.dst);
        try {
            String userId = identity.getPrincipal().getName();
            boolean isAdmin = identity.getRoles().contains("admin");
            folderService.moveFolder(projectId, userId, request.src,request.dst, isAdmin);
            logSuccess("The operation was successful");

            return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The folder was renamed")).build(); // 204
        }
        catch (PathException | IOException e) { // 400
            logError("Error 400: The source or destination path is invalid (null or empty for example)");

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
            logError("Error 409: The folder already exists");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The folder already exists")).build();
        }
    }
}