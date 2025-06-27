package fr.epita.assistants.ping.presentation.rest;


import fr.epita.assistants.ping.api.request.NewRoleRequest;
import fr.epita.assistants.ping.api.request.TopicRoleRequest;
import fr.epita.assistants.ping.api.request.UpdateRoleRequest;
import fr.epita.assistants.ping.domain.service.RoleService;
import fr.epita.assistants.ping.domain.service.TopicService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Logger;
import fr.epita.assistants.ping.utils.RequestVerifyer;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/roles")
public class RoleResource {
    @Inject
    RoleService roleService;
    @Inject
    Logger logger;
    @Inject
    public SecurityIdentity identity;
    @Inject
    TopicService topicService;


    @GET
    @Path("/{id}")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRole(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get the role " + id);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 404: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(roleService.buildGetProjectResponse(id)).build();
    }

    @GET
    @Path("/all")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRoles() {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get all the roles");
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(roleService.buildGetAllRolesResponse()).build();
    }

    @POST
    @Path("")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRole(NewRoleRequest newRoleRequest) {
        if (RequestVerifyer.isInvalid(newRoleRequest)) {
            logger.logError("Error 400: The NewRoleRequest was null, or name was empty or null");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null new role request, null or empty name")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to create a new role named " + newRoleRequest.name);
        if (roleService.roleSameNameExists(newRoleRequest.name)) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("Role already exists")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(roleService.buildCreateRoleResponse(newRoleRequest.name, false)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(@PathParam("id") Long id, UpdateRoleRequest updateRoleRequest)
    {
        if (RequestVerifyer.isInvalid(updateRoleRequest))
        {
            logger.logError("Error 400: The UpdateRoleRequest was null, or name was empty or null");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null update role request, null or empty name")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to update role " + id + "with the new name " + updateRoleRequest.newName);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 404: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        boolean canUpdateRole = roleService.canUpdate(id);
        if (!canUpdateRole)
        {
            logger.logError("Error 405: role is read only, you cannot change it");
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(new ErrorInfo("Role is read only")).build();
        }
        boolean updated = roleService.updateRole(id, updateRoleRequest.newName);
        if (!updated)
        {
            logger.logError("Error 409: role with this name already exists");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("Role with this name already exists")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteRole(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to delete role " + id);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 404: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        boolean canUpdateRole = roleService.canUpdate(id);
        if (!canUpdateRole)
        {
            logger.logError("Error 404: role is read only, you cannot change it");
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(new ErrorInfo("Role is read only")).build();
        }
        roleService.deleteRoleById(id);
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/add-topic")
    @RolesAllowed("admin")
    public Response addTopic(@PathParam("id") Long id, TopicRoleRequest topicRoleRequest) {
        if (RequestVerifyer.isInvalid(topicRoleRequest))
        {
            logger.logError("Error 400: The TopicRoleRequest was null, or topicId was invalid or null");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The TopicRoleRequest was null, or topicId was invalid or null")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add topic " + topicRoleRequest.topicId + " to role " + id);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 404: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        boolean canUpdateRole = roleService.canUpdate(id);
        if (!canUpdateRole)
        {
            logger.logError("Error 405: role is read only, you cannot add topic to it");
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(new ErrorInfo("Role is read only")).build();
        }
        if (!topicService.topicExists(topicRoleRequest.topicId))
        {
            logger.logError("Error 404: topic not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Topic not found")).build();
        }
        boolean added = roleService.addTopicToRole(id, topicRoleRequest.topicId);
        if (!added)
        {
            logger.logError("Error 409: topic already assigned to this role");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("this topic has already been added to this role")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/remove-topic")
    @RolesAllowed("admin")
    public Response removeTopic(@PathParam("id") Long id, TopicRoleRequest topicRoleRequest) {
        if (RequestVerifyer.isInvalid(topicRoleRequest)) {
            logger.logError("Error 400: The TopicRoleRequest was null, or topicId was invalid or null");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The TopicRoleRequest was null, or topicId was invalid or null")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to remove topic " + topicRoleRequest.topicId + " from role " + id);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 404: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        boolean canUpdateRole = roleService.canUpdate(id);
        if (!canUpdateRole)
        {
            logger.logError("Error 405: role is read only, cannot remove topic");
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(new ErrorInfo("Role is read only")).build();
        }
        if (!topicService.topicExists(topicRoleRequest.topicId))
        {
            logger.logError("Error 404: topic not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Topic not found")).build();
        }
        boolean deleted = roleService.deleteTopicFromRole(id, topicRoleRequest.topicId);
        if (!deleted)
        {
            logger.logError("Error 409: topic was not assigned to this role");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("Topic was not assigned to this role")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
