package fr.epita.assistants.ping.presentation.rest;

import fr.epita.assistants.ping.api.request.CreateCategoryRequest;
import fr.epita.assistants.ping.api.request.UpdateCategoryRequest;
import fr.epita.assistants.ping.api.response.CategoryResponse;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.data.model.CategoryModel;
import fr.epita.assistants.ping.data.repository.CategoryRepository;
import fr.epita.assistants.ping.domain.service.CategoryService;
import fr.epita.assistants.ping.utils.Logger;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/Category")
public class CategoryResource {
    @Inject
    CategoryService categoryService;
    @Inject
    Logger logger;
    @Inject public SecurityIdentity identity;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    /*
     *   Get all the questions/responses in the FAQ
     */
    public Response getCategory() {
        logger.logInfo(identity.getPrincipal().getName() + " request to get all Category");
        CategoryResponse[] response = categoryService.listAllCategories();
        logger.logSuccess("The operation was successful");
        return Response.ok(response, MediaType.APPLICATION_JSON).build(); // 200
    }
    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response createCategory(CreateCategoryRequest request) {
        if (request.name == null || request.name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Category name is required").build(); // 400
        }
        logger.logInfo(identity.getPrincipal().getName() + " is creating category: " + request.name);
        CategoryResponse category = categoryService.createCategory(request.name);
        logger.logSuccess("Category created: " + category.getName());
        return Response.status(Response.Status.CREATED).entity(category).build(); // 201
    }
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response updateCategoryName(@PathParam("id") Long id, UpdateCategoryRequest request) {
        if (request.name == null || request.name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("New category name is required").build(); // 400
        }

        logger.logInfo(identity.getPrincipal().getName() + " is updating category ID " + id + " to name: " + request.name);

        CategoryResponse updatedCategory = categoryService.updateCategoryName(id, request.name);
        if (updatedCategory == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build(); // 404
        }

        logger.logSuccess("Category updated: " + updatedCategory.name);
        return Response.ok(updatedCategory, MediaType.APPLICATION_JSON).build(); // 200
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("admin")
    public Response deleteCategory(@PathParam("id") Long id) {
        logger.logInfo(identity.getPrincipal().getName() + " is attempting to delete category ID " + id);

        boolean deleted = categoryService.deleteCategory(id);

        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build(); // 404
        }

        logger.logSuccess("Category deleted successfully with its related questions.");
        return Response.status(Response.Status.NO_CONTENT).build(); // 204
    }

}
