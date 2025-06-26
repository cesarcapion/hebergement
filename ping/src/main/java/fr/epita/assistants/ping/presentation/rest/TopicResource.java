package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.NewTopicRequest;
import fr.epita.assistants.ping.api.request.UpdateTopicRequest;
import fr.epita.assistants.ping.domain.service.RoleService;
import fr.epita.assistants.ping.domain.service.TopicService;
import fr.epita.assistants.ping.utils.ErrorInfo;
import fr.epita.assistants.ping.utils.RequestVerifyer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/topics")
@RolesAllowed("admin")
public class TopicResource {
    @Inject
    TopicService topicService;
    @Inject
    RoleService roleService;

    @Path("")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTopic(NewTopicRequest newTopicRequest) {
        if (RequestVerifyer.isInvalid(newTopicRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("null request, null name or empty name")).build();
        }
        if (topicService.topicSameNameExists(newTopicRequest.name)) {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("topic already exists")).build();
        }
        return Response.status(Response.Status.OK).entity(topicService.buildCreateTopicResponse(newTopicRequest.name)).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopic(@PathParam("id") Long id) {
        if (!topicService.topicExists(id))
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist")).build();
        }

        return Response.status(Response.Status.OK).entity(topicService.buildGetTopicResponse(id)).build();
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTopic(@PathParam("id") Long id, UpdateTopicRequest updateTopicRequest) {
        if (RequestVerifyer.isInvalid(updateTopicRequest)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("null request, null id, null name or empty name")).build();
        }
        if (!topicService.topicExists(id))
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist")).build();
        }
        boolean updated = topicService.updateTopic(id, updateTopicRequest.newName);
        if (!updated)
        {
            return Response.status(Response.Status.CONFLICT).entity(new ErrorInfo("new name for topic already assigned")).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/{id}")
    @DELETE
    public Response deleteTopic(@PathParam("id") Long id) {
        if (!topicService.topicExists(id))
        {
            return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo("topic does not exist, cannot delete it")).build();
        }

        topicService.deleteTopic(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTopics() {
        return Response.status(Response.Status.OK).entity(topicService.buildGetAllTopicsResponse()).build();
    }
}
