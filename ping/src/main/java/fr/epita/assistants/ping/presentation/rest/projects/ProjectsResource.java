package fr.epita.assistants.ping.presentation.rest.projects;

import fr.epita.assistants.ping.api.request.ExecFeatureRequest;
import fr.epita.assistants.ping.api.request.NewProjectRequest;
import fr.epita.assistants.ping.api.request.UpdateProjectRequest;
import fr.epita.assistants.ping.api.request.UserProjectRequest;
import fr.epita.assistants.ping.api.response.ProjectResponse;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.domain.service.ProjectMembersService;
import fr.epita.assistants.ping.domain.service.ProjectService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Feature;
import fr.epita.assistants.ping.utils.RequestVerifyer;
import fr.epita.assistants.ping.utils.UserStatus;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.UUID;

@Path("/api/projects")
public class ProjectsResource {
    @Inject
    ProjectService projectService;
    @Inject
    ProjectMembersService projectMembersService;

    @Inject public SecurityIdentity identity;


    @GET
    @Path("")
    @QueryParam("onlyOwned")
//    @Authenticated
//    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects(@DefaultValue("false") @QueryParam("onlyOwned") boolean onlyOwned) {
        System.out.println("uid " + identity.getAttribute("sub"));
        ArrayList<ProjectResponse> projectResponse = projectService.buildGetProjectsResponse("", onlyOwned);
        return Response.status(200).entity(projectResponse).build();
    }

    @POST
    @Path("")
//    @Authenticated
//    @RolesAllowed({"user", "admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(NewProjectRequest newProjectRequest) {
        System.out.println("anonymous? -> " + identity.isAnonymous());
        boolean isAuthorized = true;

        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }
//        if (newProjectRequest == null || newProjectRequest.name == null || newProjectRequest.name.isEmpty()){
        if (RequestVerifyer.isInvalid(newProjectRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The project name is invalid")).build();
        } else {
            // FIXME get the user
            UserModel user = new UserModel(UUID.randomUUID(), "", "", "", false, "");
            return Response.status(Response.Status.OK).entity(projectService.buildCreateProjectResponse(newProjectRequest.name, user)).build();
        }
    }


    @GET
    @Path("/all")
//    @Authenticated
//    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProjects() {
        boolean isAuthorized = true;
        boolean isAdmin = true;
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }
        if (isAdmin) {
            return Response.status(200).entity(projectService.buildGetAllProjectsResponse()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Cannot access this endpoint since you are not an admin")).build();
        }
    }

    @PUT
    @Path("/{id}")
//    @Authenticated
//    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") UUID projectId, UpdateProjectRequest updateProjectRequest) {
        boolean isAuthorized = true;

//        if (updateProjectRequest == null ||
//                (updateProjectRequest.name == null && updateProjectRequest.newOwnerId == null)){
        if (RequestVerifyer.isInvalid(updateProjectRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(new ErrorInfo("Bad update project request, fields are null or the request is null"))
                    .build();
        }
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }

        // FIXME get the user
        UserModel currentUser = new UserModel(UUID.fromString("eb07e67e-7115-418d-9b30-84b89e0d8840"), "", "", "", true, "");
        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());

        if (userStatus == UserStatus.NOT_A_MEMBER) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this project as you are not member of it"))
                    .build();
        }

        if (userStatus == UserStatus.MEMBER) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this project since you are only member, not owner or admin"))
                    .build();
        }

        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }

        ProjectModel updatedProject = projectService.UpdateProject(projectId, UUID.fromString(updateProjectRequest.newOwnerId), updateProjectRequest.name);
        if (updatedProject == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The new owner is not a member of this project")).build();
        }
        return Response.status(Response.Status.OK).entity(projectService.buildGetProjectResponse(updatedProject)).build();
    }

    @GET
//    @Authenticated
//    @RolesAllowed({"admin", "user"})
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") UUID projectId) {
        boolean isAuthorized = true;

        UserModel currentUser = new UserModel(UUID.fromString("eb07e67e-7115-418d-9b30-84b89e0d8840"), "", "", "", false, "");
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }

        // FIXME get the user
        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to update this project as you are not member")).build();
        }
        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }
        // else the user is either MEMBER, OWNER or ADMIN so he can access the project
        return Response.status(Response.Status.OK).entity(projectService.buildGetProjectResponseWithId(projectId)).build();
    }

    @DELETE
//    @Authenticated
//    @RolesAllowed({"admin"})
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") UUID projectId) {
        boolean isAuthorized = true;

        UserModel currentUser = new UserModel(UUID.randomUUID(), "", "", "", false, "");
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }

        // FIXME get the user
        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.NOT_A_MEMBER) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to delete this project as you are not member")).build();
        }
        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The project does not exist")).build();
        }
        // else the user is either MEMBER, OWNER or ADMIN so he can delete the project
        projectService.deleteProjectById(projectId);
        projectMembersService.deleteAllMembers(projectId);
        // when deleting the project make sure to remove all its members as well from the ProjectMembers database
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/add-user")
//    @Authenticated
//    @RolesAllowed({"user", "admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUserToProject(@PathParam("id") UUID projectId, UserProjectRequest userProjectRequest) {
        boolean isAuthorized = true;
//        if (userProjectRequest == null || userProjectRequest.userId == null || userProjectRequest.userId.isEmpty()) {
        if (RequestVerifyer.isInvalid(userProjectRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId or empty userId")).build();
        }
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }
        // FIXME get the current user
        UserModel currentUser = new UserModel(UUID.randomUUID(), "", "", "", false, "");
        // FIXME check that the user exists in the database
        boolean userExists = true;
        if (!userExists) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User not found")).build();
        }
        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER && !currentUser.getIsAdmin()) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not a member of the project nor an admin, cannot add a user to this project")).build();
        }
        // now the user is either member, owner or admin so he can add a new user to the project
        boolean added = projectMembersService.
                addUserToProject(
                        UUID.fromString(userProjectRequest.userId),
                        projectId);
        if (!added) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The user is already member of the project")).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/remove-user")
//    @Authenticated
    @RolesAllowed({"user", "admin"})
    public Response removeUserFromProject(@PathParam("id") UUID projectId, UserProjectRequest userProjectRequest) {
        boolean isAuthorized = true;
        UserModel currentUser = new UserModel(UUID.randomUUID(), "", "", "", false, "");
//        if (userProjectRequest == null || userProjectRequest.userId == null || userProjectRequest.userId.isEmpty()) {
        if (RequestVerifyer.isInvalid(userProjectRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId or empty userId")).build();
        }
        if (!isAuthorized) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Not authorized")).build();
        }
        boolean userExist = true;
        if (!userExist) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User not found")).build();
        }
        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this project as you are not member")).build();
        }
        if (userStatus == UserStatus.MEMBER) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user as you are only a member, not an admin nor the owner")).build();
        }

        // the current user is now either admin or owner so he can remove user
        // FIXME get the admin status of this userId by collecting it from the UserModel db
        UserStatus userToRemoveStatus = projectService.getUserStatus(UUID.fromString(userProjectRequest.userId), projectId, false);

        if (userToRemoveStatus == UserStatus.OWNER) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user because he currently owns the project")).build();
        }
        if (userToRemoveStatus == UserStatus.NOT_A_MEMBER) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("the user to remove is not member of this project")).build();
        }
        // boolean returned from the function below useless now as the user is a member of the project
        projectMembersService.deleteUserFromProject(UUID.fromString(userProjectRequest.userId), projectId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
//    @Authenticated
    @Path("/{id}/exec")
//    @RolesAllowed({"member", "admin", "owner"})
    public Response execFeatureFromProject(@PathParam("id") UUID projectId, ExecFeatureRequest execFeatureRequest) {
        UserModel currentUser = new UserModel(UUID.randomUUID(), "", "", "", false, "");

        if (RequestVerifyer.isInvalid(execFeatureRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Invalid request")).build();
        }

        UserStatus userStatus = projectService.getUserStatus(currentUser.getId(), projectId, currentUser.getIsAdmin());
        if (userStatus == UserStatus.ERROR) {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }

        if (userStatus == UserStatus.NOT_A_MEMBER) {
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not member of this project, cannot exec feature")).build();
        }

        boolean succeeded = projectService.execFeature(projectId, Feature.valueOfLabel(execFeatureRequest.feature)
                , execFeatureRequest.command, execFeatureRequest.params);
        if (!succeeded) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorInfo("Could not execute feature")).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
