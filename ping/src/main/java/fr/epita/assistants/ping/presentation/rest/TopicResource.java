package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.NewTopicRequest;
import fr.epita.assistants.ping.api.request.UpdateTopicRequest;
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

@Path("/api/topics")
public class TopicResource {
    @Inject
    TopicService topicService;
    @Inject
    RoleService roleService;
    @Inject public SecurityIdentity identity;
    @Inject
    Logger logger;

    @Path("")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response createTopic(NewTopicRequest newTopicRequest) {
        if (RequestVerifyer.isInvalid(newTopicRequest)) {
            logger.logError("Error 400: Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("null request, null name or empty name")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to create a topic named " + newTopicRequest.name);
        if (topicService.topicSameNameExists(newTopicRequest.name)) {
            logger.logError("Error 409: Topic name already exists");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("topic already exists")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(topicService.buildCreateTopicResponse(newTopicRequest.name)).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response getTopic(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get topic with id " + id);
        if (!topicService.topicExists(id))
        {
            logger.logError("Error 404: Topic with id " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(topicService.buildGetTopicResponse(id)).build();
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response updateTopic(@PathParam("id") Long id, UpdateTopicRequest updateTopicRequest) {
        if (RequestVerifyer.isInvalid(updateTopicRequest)) {
            logger.logError("Error 400: Invalid request");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("null request, null id, null name or empty name")).build();
        }
        logger.logInfo(identity.getPrincipal().getName() + " requested to update topic with id " + id + "with the new name " + updateTopicRequest.newName);
        if (!topicService.topicExists(id))
        {
            logger.logError("Error 404: Topic with id " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist")).build();
        }
        boolean updated = topicService.updateTopic(id, updateTopicRequest.newName);
        if (!updated)
        {
            logger.logError("Error 409: New name for the topic already exists");
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("new name for topic already assigned")).build();
        }
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/{id}")
    @DELETE
    @Authenticated
    public Response deleteTopic(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " requested to delete topic with id " + id);
        if (!topicService.topicExists(id))
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist, cannot delete it")).build();
        }
        logger.logSuccess("the operation was successful");
        topicService.deleteTopic(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response getAllTopics() {
        logger.logInfo(identity.getPrincipal().getName() + " requested to get all topics");
        logger.logSuccess("the operation was successful");
        return Response.status(Response.Status.OK).entity(topicService.buildGetAllTopicsResponse()).build();
    }
}
