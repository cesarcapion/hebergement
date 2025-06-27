package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.CreateUserRequest;
import fr.epita.assistants.ping.api.response.CreateFirstAdminResponse;
import fr.epita.assistants.ping.api.response.RoleResponse;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.domain.service.RoleService;
import fr.epita.assistants.ping.domain.service.TicketService;
import fr.epita.assistants.ping.domain.service.TopicService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.utils.DefaultRoles;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Arrays;

@Path("api/init")
public class InitResource {
    @Inject
    UserService userService;
    @Inject
    Logger logger;
    @Inject
    TopicService topicService;
    @Inject
    RoleService roleService;
    @Inject
    TicketService ticketService;

    DefaultRoles defaultRoles;

    @Path("")
    @GET
    public Response init() {
        logger.logInfo("creating first user...");
        if (Arrays.stream(userService.getAllUsers()).findAny().isPresent())
        {
            logger.logError("Error 403: not allowed to call this endpoint since there is already users in the database");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("not allowed to call this endpoint since there is already users in the database")).build();
        }
        else
        {
            UserResponse userResponse;
            ArrayList<RoleResponse> roles;
            try {
                // clear all databases
                topicService.clear();
                ticketService.clear();
                roleService.clear();
                logger.logInfo("creating admin role...");
                RoleResponse adminRoleResponse = roleService.buildCreateRoleResponse("admin", true);
                DefaultRoles.setAdminRoleId(adminRoleResponse.id);
                logger.logInfo("creating user role...");
                RoleResponse userRoleResponse = roleService.buildCreateRoleResponse("user", true);
                DefaultRoles.setUserRoleId(userRoleResponse.id);
                CreateUserRequest createUserRequest = new CreateUserRequest()
                        .withMail("admin.admin@epita.fr")
                        .withPassword("@dminPING_2025")
                        .withAdmin(true);
                userResponse = userService.create(createUserRequest);

                // create the 2 default roles user and admin
                roles = new ArrayList<>(){{add(adminRoleResponse);add(userRoleResponse);}};

            } catch (Exception e) {
                logger.logError("Error 500: error creating user: " + e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorInfo("unhandled error in /init")).build();
            }
            logger.logSuccess("user created");
            return Response.status(Response.Status.CREATED)
                    .entity(new CreateFirstAdminResponse()
                            .withCreatedRoles(roles)
                            .withUser(userResponse))
                    .build();
        }
    }
}
