package fr.epita.assistants.ping.presentation.rest.user;

import java.util.UUID;

import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.request.loginRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.api.response.loginResponse;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.errors.Exceptions.AlreadyExistException;
import fr.epita.assistants.ping.errors.Exceptions.BadInfosException;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import fr.epita.assistants.ping.utils.ErrorInfo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static fr.epita.assistants.ping.utils.Logger.*;
import static fr.epita.assistants.ping.utils.Logger.logError;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UserService userService;
    @Inject public SecurityIdentity identity;


    @POST
    @RolesAllowed("admin")
    public Response createUser(CreateUserRequest user) {

        logInfo("Trying to create the user " + user.login);

        try {
            logSuccess("The operation was successful");
            UserResponse response = userService.create(user);
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        } catch (InvalidException e) { // 400
            logError("Error 400: The login or the password is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The login or the password is invalid")).build();
        }catch (AlreadyExistException e) { // 409
            logError("Error 409: The login is already taken");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The login is already taken")).build();
        }

    }

    @GET
    @Path("/all")
    @RolesAllowed("admin")
    public Response listUsers() {
        logInfo("Trying to get all users");
        UserResponse[] response = userService.getAllUsers();
        logSuccess("The operation was successful");
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
    }


    //login a user
    @POST
    @Path("/login")
//    @RolesAllowed("admin")
    public Response loginUser(loginRequest request) {
        try
        {
        logInfo("Trying to connect a user");
        System.out.println("Trying to connect a user");
        loginResponse response = userService.loginUser(request.login,request.password);
        logSuccess("The operation was successful");
        System.out.println("The operation was successful");
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (InvalidException e) { // 400
            logError("Error 400: The login or the password is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The login or the password is invalid")).build();
        }
        catch (BadInfosException e) // 401
        {
            System.out.println("NIMPORT QUOI FRERE T CON OU QUOIIII");
            logError("Error 401: The login/password combination is invalid");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("The login or the password is invalid")).build();
        }
    }


    @GET
    @Path("/{id}")
    @RolesAllowed({"admin","user"})
    public Response refreshToken() {
        try
        {
            logInfo("Trying to refresh the user token");
            loginResponse response = userService.refreshToken(identity.getPrincipal().getName());
            logSuccess("The operation was successful");
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (UserException e) // 404
        {
            logError("Error 404: The user could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The user could not be found")).build();
        }
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