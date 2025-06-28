package fr.epita.assistants.ping.presentation.rest;


import fr.epita.assistants.ping.api.request.NewTicketHistoryRequest;
import fr.epita.assistants.ping.domain.service.TicketHistoryService;
import fr.epita.assistants.ping.domain.service.TicketService;
import fr.epita.assistants.ping.domain.service.UserService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Logger;
import fr.epita.assistants.ping.utils.RequestVerifyer;
import fr.epita.assistants.ping.utils.TicketStatus;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import java.util.UUID;


@Path("/api/ticket-history")
public class TicketHistoryResource {
    @Inject
    TicketService ticketService;

    @Inject
    TicketHistoryService ticketHistoryService;

    @Inject
    UserService userService;

    @Inject
    public SecurityIdentity identity;
    @Inject
    Logger logger;

    @POST
    @Path("/{ticketId}")
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addHistory(@PathParam("ticketId") UUID ticketId, NewTicketHistoryRequest historyRequest) {
        if (RequestVerifyer.isInvalid(historyRequest)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("null request, content path null, invalid content path or invalid resource path if defined"))
                    .build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to add ticket history, ticketId: " + ticketId
                + ", resource path: " + historyRequest.resourcePath + ", content path " + historyRequest.contentPath);
        if (ticketService.DoesNotExist(ticketId)) {
            logger.logError("Error 404: ticket not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorInfo("Ticket not found"))
                    .build();
        }
        if (ticketService.getTicketStatus(ticketId) == TicketStatus.RESOLVED)
        {
            logger.logError("Error 405: ticket is closed");
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(new ErrorInfo("Ticket is closed")).build();
        }
        boolean created = ticketHistoryService.addHistory(historyRequest.contentPath, historyRequest.resourcePath
                , ticketId, UUID.fromString(identity.getPrincipal().getName()));
        if (!created)
        {
            logger.logError("Error 400: content or resource path invalid");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("content or resource path invalid")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{ticketId}")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory(@PathParam("ticketId") UUID ticketId) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get history of ticketId: " + ticketId);
        if (ticketService.DoesNotExist(ticketId))
        {
            logger.logError("Error 404: ticket not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Ticket not found")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(ticketHistoryService.getHistory(ticketId)).build();
    }

    @GET
    @Path("/stats/{mail}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStat(@PathParam("mail") String mail) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get stats of : " + mail);
        if (!userService.mailExist(mail))
        {
            logger.logError("Error 404: mail not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("Mail not found")).build();
        }
        if (userService.isUser(mail))
        {
            logger.logError("Error 400: this mail is from a user");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Mail from a user")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(ticketHistoryService.getStat(mail)).build();
    }

}
