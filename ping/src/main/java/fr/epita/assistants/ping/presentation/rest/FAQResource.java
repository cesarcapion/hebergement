package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.FAQRequest;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.domain.service.FAQService;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    @Authenticated
    /*
     *   Create a new question/response in the FAQ
     */
    public Response postFAQ(FAQRequest request) {
        logger.logInfo(identity.getPrincipal().getName() + " request to add a question with a response");
        FAQResponse response = FAQService.createQuestion(request);
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
    }
}
