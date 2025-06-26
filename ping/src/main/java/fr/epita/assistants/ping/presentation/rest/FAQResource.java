package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.FAQRequest;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.domain.service.FAQService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/FAQ")
public class FAQResource {
    @Inject
    Logger logger;

    @Inject public SecurityIdentity identity;
    @Inject
    FAQService FAQService;


    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    /*
    *   Get all the questions/responses in the FAQ
    */
    public Response getFAQ() {
        logger.logInfo(identity.getPrincipal().getName() + " request to get the FAQ");
        FAQResponse[] response = FAQService.getAll();
        logger.logSuccess("The operation was successful");
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    /*
     *   Create a new question and response in the FAQ
     */
    public Response postFAQ(FAQRequest request) {
        logger.logInfo(identity.getPrincipal().getName() + " request to add a question with a response");
        try {
            FAQResponse response = FAQService.createQuestion(request);
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (NotFoundException e) {
            logger.logError("Error 404: error while creating FAQ");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("This Category id does not exist ")).build(); // 404
        }

    }

    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    /*
     *   Modify an existing FAQ entry
     */
    public Response updateFAQ(FAQRequest request) {
        try {
            logger.logInfo(identity.getPrincipal().getName() + " requested to update a FAQ entry");
            FAQResponse response = FAQService.updateQuestion(request);
            logger.logSuccess("FAQ entry updated successfully");
            return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
        }
        catch (NotFoundException e) {
            logger.logError("Error 404: error while updating FAQ");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("This FAQ or category id does not exist ")).build(); // 404
        }

    }

    @DELETE
    @Path("{FAQid}")
    @RolesAllowed("admin")
    /*
     *   Delete an existing FAQ entry
     */
    public Response deleteFAQ(@PathParam("FAQid") long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to delete a FAQ entry");
        if(!FAQService.deleteFAQ(id))
        {
            logger.logError("Error 404: error while deleting FAQ");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("This FAQ id does not exist")).build(); // 404
        }
        logger.logSuccess("FAQ entry deleted successfully");
        return Response.status(Response.Status.NO_CONTENT).entity(new ErrorInfo("The FAQ was deleted")).build(); // 204
    }
}