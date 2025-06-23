package fr.epita.assistants.ping.presentation.rest.user;

import java.io.InputStream;
import java.util.UUID;


import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.request.UserUpdateRequest;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.api.response.LoginResponse;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.api.request.LoginRequest;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.errors.Exceptions.AlreadyExistException;
import fr.epita.assistants.ping.errors.Exceptions.BadInfosException;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.NotAuthorizedException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static fr.epita.assistants.ping.utils.Logger.*;

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    UserService userService;
    @Inject public SecurityIdentity identity;
    @Inject
    Logger logger;

    @POST
    @RolesAllowed("admin")
    public Response createUser(CreateUserRequest user) {

        logger.logInfo("User with ID " + identity.getPrincipal().getName() + " is trying to create the user: login: " + user.login + " ,password: " + user.password + " admin: " + user.isAdmin);

        try {
            logger.logSuccess("The operation was successful : " );
            UserResponse response = userService.create(user);
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        } catch (InvalidException e) { // 400
            logger.logError("Error 400: The login or the password is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The login or the password is invalid")).build();
        }catch (AlreadyExistException e) { // 409
            logger.logError("Error 409: The login is already taken");

            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The login is already taken")).build();
        }

    }

    @GET
    @Path("/all")
    @RolesAllowed("admin")
    public Response listUsers() {
        logger.logInfo(identity.getPrincipal().getName() + " is trying to get all users");
        UserResponse[] response = userService.getAllUsers();
        logger.logSuccess("The operation was successful");
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
    }


    //login a user
    @POST
    @Path("/login")
    //@RolesAllowed("admin")
    public Response loginUser(LoginRequest request) {

        try
        {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("privateKey.txt");
            //System.out.println("Key found? " + (is != null));
            logger.logInfo("User is trying to connect as \"" + request.login + "\"");
            LoginResponse response = userService.loginUser(request.login,request.password);
            logger.logSuccess(identity.getPrincipal().getName() + " successfully logged in : token : " + response.token);
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (InvalidException e) { // 400
            logger.logError("Error 400: The login or the password is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The login or the password is invalid")).build();
        }
        catch (BadInfosException e) // 401
        {
            logger.logError("Error 401: The login/password combination is invalid");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("The login/password combination is invalid")).build();
        }
    }


    @GET
    @Path("/refresh")
    @RolesAllowed({"admin","user"})
    public Response refreshToken() {
        try
        {
            logger.logInfo(identity.getPrincipal().getName() + " is trying to refresh his token");
            LoginResponse response = userService.refreshToken(UUID.fromString(identity.getPrincipal().getName()));
            logger.logSuccess("The operation was successful");
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (UserException e) // 404
        {
            logger.logError("Error 404: The user could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The user could not be found")).build();
        }
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin","user"})
    public Response updateUser(UserUpdateRequest user,@PathParam("id") UUID id) {
        try
        {
            logger.logInfo(identity.getPrincipal().getName() + " is trying to update " + id + " infos : displayName:" + user.displayName + ", password : (tu l'auras pas), avatar : " + user.avatar);
            UserResponse response = userService.update(UUID.fromString(identity.getPrincipal().getName()),id,user);
            logger.logSuccess("The operation was successful");
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (NotAuthorizedException e) // 403
        {
            logger.logError("Error 403: The user is not allowed");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed")).build();
        }
        catch (UserException e) // 404
        {
            logger.logError("Error 404: The user could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The user could not be found")).build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin","user"})
    public Response getUser(@PathParam("id") UUID id) {
        try
        {
            logger.logInfo(identity.getPrincipal().getName() + " is trying to get " + id + " infos");
            UserResponse response = userService.get(UUID.fromString(identity.getPrincipal().getName()),id);
            logger.logSuccess("The operation was successful");
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (NotAuthorizedException e) // 403
        {
            logger.logError("Error 403: The user is not allowed");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access this user")).build();
        }
        catch (UserException e) // 404
        {
            logger.logError("Error 404: The user could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User not found")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin","user"})
    public Response deleteUser(@PathParam("id") UUID id) {
        try
        {
            logger.logInfo(identity.getPrincipal().getName() + " is trying to delete " + id);
            userService.delete(id,UUID.fromString(identity.getPrincipal().getName()));
            logger.logSuccess("The operation was successful");
            return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The user was deleted")).build(); // 204
        }
        catch (NotAuthorizedException e) // 403
        {
            logger.logError("Error 403: The user is not allowed");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("The user is not allowed to access this endpoint, or the user owns projects")).build();
        }
        catch (UserException  | IllegalArgumentException e) // 404
        {
            logger.logError("Error 404: The user could not be found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The user could not be found")).build();
        }
    }
}