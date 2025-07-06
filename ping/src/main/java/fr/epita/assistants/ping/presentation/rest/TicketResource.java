package fr.epita.assistants.ping.presentation.rest;


import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


import fr.epita.assistants.ping.api.request.ExecFeatureRequest;
import fr.epita.assistants.ping.api.request.NewTicketRequest;
import fr.epita.assistants.ping.api.request.UpdateTicketRequest;
import fr.epita.assistants.ping.api.request.UserTicketRequest;
import fr.epita.assistants.ping.api.response.TicketResponse;

import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.TopicModel;
import fr.epita.assistants.ping.data.model.UserModel;

import fr.epita.assistants.ping.domain.service.TicketService;
import fr.epita.assistants.ping.domain.service.TopicService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.utils.*;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/tickets")
public class TicketResource {
    @Inject
    Logger logger;
    @Inject
    TicketService ticketService;
    @Inject
    UserService userService;

    @Inject public SecurityIdentity identity;
    @Inject
    TopicService topicService;


    @GET
    @Path("")
    @QueryParam("onlyOwned")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTickets(@DefaultValue("false") @QueryParam("onlyOwned") boolean onlyOwned,
                               @DefaultValue("false") @QueryParam("descending") boolean descending,
                               @DefaultValue("NONE") @QueryParam("filter") TicketStatus filter,
                               @DefaultValue("NONE") @QueryParam("sorting") TicketSortingStrategy sortingStrategy)
    {

        logger.logInfo(identity.getPrincipal().getName() + " requested to get all the tickets where he is "
                + (onlyOwned ? "an owner" : "a member") + "with filter: " + filter +
                " and sorting strategy: " + sortingStrategy + "and descending: " + descending);

        ArrayList<TicketResponse> ticketResponse = ticketService.buildGetTicketsResponse(identity.getPrincipal().getName(), onlyOwned, descending, filter, sortingStrategy);
        logger.logSuccess("The operation was successful");
        return Response.status(200).entity(ticketResponse).build();
    }

    @POST
    @Path("")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTicket(NewTicketRequest newTicketRequest) {
        if (RequestVerifyer.isInvalid(newTicketRequest)) {
            logger.logError("Error 400: The ticket name is invalid");

            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("The ticket name is invalid")).build();
        } else {
            logger.logInfo(identity.getPrincipal().getName() + " requested to create a new ticket named " + newTicketRequest.subject);
            UserModel user = userService.get(UUID.fromString(identity.getPrincipal().getName()));
            if (!topicService.topicExists(newTicketRequest.topicId))
            {
                logger.logError("Error 404: The topic does not exist");
                return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Topic does not exist")).build();
            }

            logger.logSuccess("The operation was successful");
            return Response.status(Response.Status.OK).entity(ticketService.buildCreateTicketResponse(newTicketRequest.subject,
                    user, topicService.getTopicById(newTicketRequest.topicId))).build();
        }
    }


    @GET
    @Path("/all")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTickets() {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get all tickets");
        logger.logSuccess("The operation was successful");
        return Response.status(200).entity(ticketService.buildGetAllTicketsResponse()).build();
    }

    @PUT
    @Path("/{id}")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTicket(@PathParam("id") UUID ticketId, UpdateTicketRequest updateTicketRequest) {
        // FIXME great change to implement in the future: if the topic of the ticket changes,
        //  all the devs on this ticket will be kicked out of this ticket if they cannot handle the new topic
        //  and the ownership of the ticket will be given back to the user, placing the ticket to PENDING status again,
        //  however if one dev in the ticket can still handle this topic, the ownership will be transfered to him if needed
        if (RequestVerifyer.isInvalid(updateTicketRequest)) {
            logger.logError("Error 400: Bad update ticket request, fields are null or the request is null");
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(new ErrorInfo("Bad update ticket request, fields are null or the request is null"))
                    .build();
        }

        logger.logInfo(identity.getPrincipal().getName() + " requested to update the ticket with id " + ticketId
                + " new name: " + updateTicketRequest.subject + "new owner: " + updateTicketRequest.newOwnerId
                + " new ticket status: " + updateTicketRequest.ticketStatus);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, Objects.equals(currentUser.getRole().getName(), "admin"));
        if (userStatus == UserStatus.NOT_A_MEMBER && !Objects.equals(currentUser.getRole().getName(), "admin")) {
            logger.logError("Error 403: Not allowed to update this ticket as you are not member of it");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this ticket as you are not member of it"))
                    .build();
        }

        if (userStatus == UserStatus.MEMBER && !Objects.equals(currentUser.getRole().getName(), "admin")) {
            logger.logError("Error 403: Not allowed to update this ticket since you are only member, not owner or admin");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new ErrorInfo("Not allowed to update this ticket since you are only member, not owner or admin"))
                    .build();
        }
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The ticket does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The ticket does not exist")).build();
        }
        if (updateTicketRequest.newTopicId != null && !topicService.topicExists(updateTicketRequest.newTopicId))
        {
            logger.logError("Error 404: The topic does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Topic does not exist")).build();
        }
        TopicModel newTopic = updateTicketRequest.newTopicId != null ? topicService.getTopicById(updateTicketRequest.newTopicId) : null;
        UserModel newOwner = updateTicketRequest.newOwnerId != null ? userService.get(UUID.fromString(updateTicketRequest.newOwnerId)) : null;
        TicketModel updatedProject = ticketService.UpdateTicket(ticketId, newOwner,
                updateTicketRequest.subject, updateTicketRequest.ticketStatus, newTopic);
        if (updatedProject == null) {
            logger.logError("Error 404: The new owner is not a member of this ticket, or the new owner does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The new owner is not a member of this ticket, or the new owner does not exist")).build();
        }
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.OK).entity(ticketService.buildGetTicketResponse(updatedProject)).build();
    }

    @GET
    @Authenticated
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicket(@PathParam("id") UUID ticketId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get the ticket with id " + ticketId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));

        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, identity.getRoles().contains("admin"));
        if (userStatus == UserStatus.NOT_A_MEMBER && !identity.getRoles().contains("admin")) {
            logger.logError("Error 403: The new owner is not a member of this ticket");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to get this ticket as you are not member")).build();
        }
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The ticket does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The ticket does not exist")).build();
        }
        // else the user is either MEMBER, OWNER or ADMIN so he can access the ticket
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.OK).entity(ticketService.buildGetTicketResponseWithId(ticketId)).build();
    }

    @DELETE
    @Authenticated
    @Path("/{id}")
    public Response deleteTicket(@PathParam("id") UUID ticketId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to delete the ticket with id " + ticketId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));

        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, Objects.equals(currentUser.getRole().getName(), "admin"));
        if (!Objects.equals(currentUser.getRole().getName(), "admin")) {
            if (userStatus == UserStatus.NOT_A_MEMBER) {
                logger.logError("Error 403: Not allowed to delete this ticket as you are not member");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to delete this ticket as you are not member")).build();
            }
            if (userStatus == UserStatus.MEMBER) {
                logger.logError("Error 403: Not allowed to delete this ticket as you are only a member");
                return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to delete this ticket as you are only a member")).build();
            }
        }
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: The ticket does not exist");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("The ticket does not exist")).build();
        }
        // else the user is either OWNER or ADMIN so he can delete the ticket
        ticketService.deleteTicketById(ticketId);
        logger.logSuccess("The operation was successful");
        // when deleting the ticket make sure to remove all its members as well from the ProjectMembers database
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/add-user")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUserToTicket(@PathParam("id") UUID ticketId, UserTicketRequest userTicketRequest) {
        if (RequestVerifyer.isInvalid(userTicketRequest)) {
            logger.logError("Error 400: Null request, null userId, empty userId or invalid uuid for userId");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId, empty userId or invalid uuid for userId")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add a user " + userTicketRequest.userId + " to the ticket with id " + ticketId);

        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        boolean userExists = userService.get(UUID.fromString(userTicketRequest.userId)) != null;
        if (!userExists) {
            logger.logError("Error 404: User to add not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User to add not found")).build();
        }
        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, Objects.equals(currentUser.getRole().getName(), "admin"));
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER && !Objects.equals(currentUser.getRole().getName(), "admin")) {
            logger.logError("Error 403: Not a member of the ticket nor an admin, cannot add a user to this ticket");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not a member of the ticket nor an admin, cannot add a user to this ticket")).build();
        }
        // now the user is either member, owner or admin so he can add a new user to the ticket
        boolean added = ticketService.addUserToTicket(ticketId, userService.get(UUID.fromString(userTicketRequest.userId)));
        if (!added) {
            logger.logError("Error 409: The user is already member of the ticket");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("The user is already member of the ticket")).build();
        }

        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/remove-user")
    @Authenticated
    public Response removeUserFromTicket(@PathParam("id") UUID ticketId, UserTicketRequest userTicketRequest) {
        if (RequestVerifyer.isInvalid(userTicketRequest)) {
            logger.logError("Error 400: Null request, null userId, empty userId or invalid uuid for userId");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Null request, null userId, empty userId or invalid uuid for userId")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to remove user " + userTicketRequest.userId + " from the ticket with id " + ticketId);
        UserModel targetedUser = userService.get(UUID.fromString(userTicketRequest.userId));
        boolean userExist = targetedUser != null;
        if (!userExist) {
            logger.logError("Error 404: User to remove not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("User to remove not found")).build();
        }
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, false);

        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER && !Objects.equals(currentUser.getRole().getName(), "admin")) {
            logger.logError("Error 403: Not allowed to remove this user from the ticket as you are not member");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user from the ticket as you are not member")).build();
        }
        if (userStatus == UserStatus.MEMBER && !Objects.equals(currentUser.getRole().getName(), "admin")) {
            logger.logError("Error 403: Not allowed to remove this user as you are only a member, not an admin nor the owner");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user as you are only a member, not an admin nor the owner")).build();
        }

        // the current user is now either admin or owner so he can remove user
        UserStatus userToRemoveStatus = ticketService.getUserStatus(targetedUser, ticketId, Objects.equals(currentUser.getRole().getName(), "admin"));

        if (userToRemoveStatus == UserStatus.OWNER) {
            logger.logError("Error 403: Not allowed to remove this user because he currently owns the ticket");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not allowed to remove this user because he currently owns the ticket")).build();
        }
        if (userToRemoveStatus == UserStatus.NOT_A_MEMBER) {
            logger.logError("Error 404: the user to remove is not member of this ticket");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("the user to remove is not member of this ticket")).build();
        }
        // boolean returned from the function below useless now as the user is a member of the ticket
        ticketService.deleteUserFromTicket(ticketId, targetedUser);
        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/exec")
    @Authenticated
    public Response execFeatureFromTicket(@PathParam("id") UUID ticketId, ExecFeatureRequest execFeatureRequest) {
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        boolean isAdmin = Objects.equals(currentUser.getRole().getName(), "admin");
        if (RequestVerifyer.isInvalid(execFeatureRequest)) {
            logger.logError("Error 400: Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Invalid request")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add a user to the ticket with id " + ticketId
                + "executing with feature: " + execFeatureRequest.feature + " ; " + "command: " + execFeatureRequest.command + " ; "
                + "parameters: " + execFeatureRequest.params );
        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, false);
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: Project not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Project not found")).build();
        }

        if (userStatus == UserStatus.NOT_A_MEMBER && !isAdmin) {
            logger.logError("Error 403: Not member of this ticket, cannot exec feature");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not member of this ticket, cannot exec feature")).build();
        }
        // member, owner or admin
        boolean succeeded = ticketService.execFeature(ticketId, Feature.valueOfLabel(execFeatureRequest.feature)
                , execFeatureRequest.command, execFeatureRequest.params);
        if (!succeeded) {
            logger.logError("Error 500: Could not execute feature");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorInfo("Could not execute feature")).build();
        }

        logger.logSuccess("The operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/leave")
    @Authenticated
    public Response leaveProject(@PathParam("id") UUID ticketId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to leave the project " + ticketId);
        UserModel currentUser = userService.get(UUID.fromString(identity.getPrincipal().getName()));
        UserStatus userStatus = ticketService.getUserStatus(currentUser, ticketId, false);
        if (userStatus == UserStatus.ERROR) {
            logger.logError("Error 404: ticket not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Ticket not found")).build();
        }
        if (userStatus == UserStatus.NOT_A_MEMBER) {
            logger.logError("Error 404: Not member of this ticket, cannot leave");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Not member of this ticket, cannot leave")).build();
        }
        if (userStatus == UserStatus.OWNER) {
            logger.logError("Error 404: Owner of this ticket, cannot leave");
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorInfo("Owner of this ticket, cannot leave")).build();
        }
        logger.logSuccess("The operation was successful");
        ticketService.deleteUserFromTicket(ticketId, currentUser);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}