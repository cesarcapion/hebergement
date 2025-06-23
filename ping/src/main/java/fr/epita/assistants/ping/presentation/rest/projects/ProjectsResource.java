package fr.epita.assistants.ping.presentation.rest.projects;


import java.util.ArrayList;
import java.util.UUID;


import fr.epita.assistants.ping.api.request.ExecFeatureRequest;
import fr.epita.assistants.ping.api.request.NewProjectRequest;
import fr.epita.assistants.ping.api.request.UpdateProjectRequest;
import fr.epita.assistants.ping.api.request.UserProjectRequest;
import fr.epita.assistants.ping.api.response.ProjectResponse;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;

import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.utils.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/projects")
public class ProjectsResource {
    @Inject
    Logger logger;
    @Inject
    ProjectService projectService;
    @Inject
    UserService userService;

    @Inject public SecurityIdentity identity;


    @GET
    @Path("")
    @QueryParam("onlyOwned")
    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects(@DefaultValue("false") @QueryParam("onlyOwned") boolean onlyOwned) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get all the projects where he is " + (onlyOwned ? "an owner" : "a member"));

        ArrayList<ProjectResponse> projectResponse = projectService.buildGetProjectsResponse(identity.getPrincipal().getName(), onlyOwned);
        logger.logSuccess("The operation was successful");
        return Response.status(200).entity(projectResponse).build();
    }

    @POST
    @Path("")
    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(NewProjectRequest newProjectRequest) {
        if (RequestVerifyer.isInvalid(newProjectRequest)) {
            logger.logError("Error 400: The project name is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The project name is invalid")).build();
        } else {
            logger.logInfo(identity.getPrincipal().getName() + " requested to create a new project named " + newProjectRequest.name);
            UserModel user = userService.get(UUID.fromString(identity.getPrincipal().getName()));
            logger.logSuccess("The operation was successful");
            return Response.status(Response.Status.OK).entity(projectService.buildCreateProjectResponse(newProjectRequest.name, user)).build();
        }
    }


    @GET
    @Path("/all")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects() {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get all projects");
        logger.logSuccess("The operation was successful");
        return Response.status(200).entity(projectService.buildGetAllProjectsResponse()).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") UUID projectId, UpdateProjectRequest updateProjectRequest) {
        if (RequestVerifyer.isInvalid(updateProjectRequest)) {
            logger.logError("Error 400: Bad update project request, fields are null or the request is null");
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(new ErrorInfo("Bad update project request, fields are null or the request is null"))
                    .build();
        }

        logger.logInfo(identity.getPrincipal().getName() + " requested to update the project with id " + projectId
                + " new name: " + updateProjectRequest.name + "new owner: " + updateProjectRequest.newOwnerId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, currentUser.getIsAdmin());

        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: Not allowed to update this project as you are not member of it");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this project as you are not member of it"))
                    .build();
        }

        if (userStatus == UserStatus.MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: Not allowed to update this project since you are only member, not owner or admin");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this project since you are only member, not owner or admin"))
                    .build();
        }

        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The project does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }
        UserModel newOwner = updateProjectRequest.newOwnerId != null ? userService.get(UUID.fromString(updateProjectRequest.newOwnerId)) : null;
        ProjectModel updatedProject = projectService.UpdateProject(projectId, newOwner, updateProjectRequest.name);
        if (updatedProject == null) {
            logger.logError("Error 404: The new owner is not a member of this project, or the new owner does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The new owner is not a member of this project, or the new owner does not exist")).build();
        }
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.OK).entity(projectService.buildGetProjectResponse(updatedProject)).build();
    }

    @GET
    @RolesAllowed({"admin", "user"})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") UUID projectId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get the project with id " + projectId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));

        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: The new owner is not a member of this project");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to get this project as you are not member")).build();
        }
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The project does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }
        // else the user is either MEMBER, OWNER or ADMIN so he can access the project
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.OK).entity(projectService.buildGetProjectResponseWithId(projectId)).build();
    }

    @DELETE
    @RolesAllowed({"admin", "user"})
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") UUID projectId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to delete the project with id " + projectId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));

        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, currentUser.getIsAdmin());
        if (!currentUser.getIsAdmin()) {
            if (userStatus == UserStatus.NOT_A_MEMBER) {
                logger.logError("Error 403: Not allowed to delete this project as you are not member");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to delete this project as you are not member")).build();
            }
            if (userStatus == UserStatus.MEMBER) {
                logger.logError("Error 403: Not allowed to delete this project as you are only a member");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to delete this project as you are only a member")).build();
            }
        }
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The project does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }
        // else the user is either OWNER or ADMIN so he can delete the project
        projectService.deleteProjectById(projectId);
//        projectMembersService.deleteAllMembers(projectId);
        logger.logSuccess("The operation was successful");
        // when deleting the project make sure to remove all its members as well from the ProjectMembers database
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/add-user")
    @RolesAllowed({"user", "admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUserToProject(@PathParam("id") UUID projectId, UserProjectRequest userProjectRequest) {
        if (RequestVerifyer.isInvalid(userProjectRequest)) {
            logger.logError("Error 400: Null request, null userId, empty userId or invalid uuid for userId");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId, empty userId or invalid uuid for userId")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add a user " + userProjectRequest.userId + " to the project with id " + projectId);

        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        boolean userExists = userService.get(UUID.fromString(userProjectRequest.userId)) != null;
        if (!userExists) {
            logger.logError("Error 404: User to add not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User to add not found")).build();
        }
        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: Not a member of the project nor an admin, cannot add a user to this project");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not a member of the project nor an admin, cannot add a user to this project")).build();
        }
        // now the user is either member, owner or admin so he can add a new user to the project
        boolean added = projectService.addUserToProject(projectId, userService.get(UUID.fromString(userProjectRequest.userId)));
        if (!added) {
            logger.logError("Error 409: The user is already member of the project");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The user is already member of the project")).build();
        }

        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/remove-user")
    @RolesAllowed({"user", "admin"})
    public Response removeUserFromProject(@PathParam("id") UUID projectId, UserProjectRequest userProjectRequest) {
        if (RequestVerifyer.isInvalid(userProjectRequest)) {
            logger.logError("Error 400: Null request, null userId, empty userId or invalid uuid for userId");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId, empty userId or invalid uuid for userId")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to remove user " + userProjectRequest.userId + " from the project with id " + projectId);
        UserModel targetedUser = userService.get(UUID.fromString(userProjectRequest.userId));
        boolean userExist = targetedUser != null;
        if (!userExist) {
            logger.logError("Error 404: User to remove not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User to remove not found")).build();
        }
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, false);

        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: Not allowed to remove this user from the project as you are not member");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user from the project as you are not member")).build();
        }
        if (userStatus == UserStatus.MEMBER && !currentUser.getIsAdmin()) {
            logger.logError("Error 403: Not allowed to remove this user as you are only a member, not an admin nor the owner");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user as you are only a member, not an admin nor the owner")).build();
        }

        // the current user is now either admin or owner so he can remove user
        UserStatus userToRemoveStatus = projectService.getUserStatus(targetedUser, projectId, targetedUser.getIsAdmin());

        if (userToRemoveStatus == UserStatus.OWNER) {
            logger.logError("Error 403: Not allowed to remove this user because he currently owns the project");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user because he currently owns the project")).build();
        }
        if (userToRemoveStatus == UserStatus.NOT_A_MEMBER) {
            logger.logError("Error 404: the user to remove is not member of this project");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("the user to remove is not member of this project")).build();
        }
        // boolean returned from the function below useless now as the user is a member of the project
//        projectMembersService.deleteUserFromProject(UUID.fromString(userProjectRequest.userId), projectId);
        projectService.deleteUserFromProject(projectId, targetedUser);
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/exec")
    @RolesAllowed({"admin", "user"})
    public Response execFeatureFromProject(@PathParam("id") UUID projectId, ExecFeatureRequest execFeatureRequest) {
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        boolean isAdmin = currentUser.getIsAdmin();
        if (RequestVerifyer.isInvalid(execFeatureRequest)) {
            logger.logError("Error 400: Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Invalid request")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add a user to the project with id " + projectId
                + "executing with feature: " + execFeatureRequest.feature + " ; " + "command: " + execFeatureRequest.command + " ; "
                + "parameters: " + execFeatureRequest.params );
        UserStatus userStatus = projectService.getUserStatus(currentUser, projectId, false);
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }

        if (userStatus == UserStatus.NOT_A_MEMBER && !isAdmin) {
            logger.logError("Error 403: Not member of this project, cannot exec feature");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not member of this project, cannot exec feature")).build();
        }
        // member, owner or admin
        boolean succeeded = projectService.execFeature(projectId, Feature.valueOfLabel(execFeatureRequest.feature)
                , execFeatureRequest.command, execFeatureRequest.params);
        if (!succeeded) {
            logger.logError("Error 500: Could not execute feature");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorInfo("Could not execute feature")).build();
        }

        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}