package fr.epita.assistants.ping.presentation.rest.filesystem;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.utils.Logger;

@Path("/api/projects/{projectId}")
@Produces(MediaType.APPLICATION_OCTET_STREAM) // pour les fichiers binaires
public class NON {

    @GET
    @Path("/files")
    @RolesAllowed({"user", "admin"}) // Vérifie que l'utilisateur est connecté
    public Response getFile(@PathParam("projectId") UUID projectId,
                            @QueryParam("path") String path,
                            @Context SecurityContext securityContext) {

        try {
            ProjectModel project = checkAccess(projectId, securityContext);

            Path basePath = Paths.get(project.getPath()).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(path).normalize();

            if (!filePath.startsWith(basePath)) {
                Logger.error("Path traversal detected: " + filePath);
                return Response.status(Response.Status.FORBIDDEN)
                               .entity("Path traversal detected.")
                               .build();
            }

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("File not found.")
                               .build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);

            Logger.log("User accessed file: " + filePath);

            return Response.ok(fileContent)
                           .type(MediaType.APPLICATION_OCTET_STREAM)
                           .build();

        } catch (IOException e) {
            Logger.error("Failed to read file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Could not read file.")
                           .build();
        } catch (SecurityException se) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("Access denied.")
                           .build();
        }
    }

    private ProjectModel checkAccess(UUID projectId, SecurityContext ctx) {
        // À toi d'implémenter :
        // - récupérer le projet par projectId
        // - vérifier si ctx.getUserPrincipal() est owner/membre/admin
        // - sinon → throw new SecurityException()
        return null; // temporaire
    }
}


