package fr.epita.assistants.ping.presentation.rest;


import fr.epita.assistants.ping.api.request.NewRoleRequest;
import fr.epita.assistants.ping.api.request.UpdateRoleRequest;
import fr.epita.assistants.ping.domain.service.RoleService;
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
public class RolesResource {
    @Inject
    RoleService roleService;
    @Inject
    Logger logger;
    @Inject
    public SecurityIdentity identity;


    @GET
    @Path("/{id}")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRole(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get the role " + id);
        if (!roleService.roleExists(id))
        {
            logger.logError("Error 400: role not found");
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
        return Response.status(Response.Status.OK).entity(roleService.buildCreateRoleResponse(newRoleRequest.name)).build();
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
            logger.logError("Error 400: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }

        if (roleService.roleSameNameExists(updateRoleRequest.newName))
        {
            logger.logError("Error 409: role with this name already exists");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("Role with this name already exists")).build();
        }
        roleService.updateRole(id, updateRoleRequest.newName);
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
            logger.logError("Error 400: role not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Role not found")).build();
        }
        roleService.deleteRoleById(id);
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
