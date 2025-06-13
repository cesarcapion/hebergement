package fr.epita.assistants.ping.presentation.rest;

import java.util.List;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.domain.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    //@Inject
    UserService userService = new UserService();


    @POST
    @RolesAllowed("admin")
    public Response createUser(UserModel user) {
        return Response.status(200).entity(userService.create(user)).build();
    }

    @GET
    @Path("/all")
    public List<UserModel> listUsers() {
        return userService.getAll();
    }

    //login a user 

    //refresh a token 


    @PUT
    @Path("/{id}")
    public UserModel updateUser(@PathParam("id") UUID id, UserModel user) {
        return userService.update(id, user);
    }

    //get a user
    @GET
    @Path("/{id}")
    public UserModel getUser(@PathParam("id") UUID id) {
        return userService.get(id);
    }

    /*@DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response deleteUser(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }*/
}