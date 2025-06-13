package fr.epita.assistants.ping.domain.executor;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.NotAuthorizedException;
import fr.epita.assistants.ping.errors.Exceptions.PathException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@ApplicationScoped
public class FileService {
    private boolean isMember(String userId, UUID projectID)
    {
        // FIXME:Doit check que le user est membre du projet
        return true;
    }

    private boolean isInvalidPath(UUID projectID, String path)
    {
        // FIXME: Doit check si le project ID existe
        return path == null || path.isBlank();
    }

    boolean isPathTraversal(String path, UUID projectID)
    {
        Path basePath = Paths.get("/var/www/projects", projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();
        return !requestedPath.startsWith(basePath);
    }

    public byte[] file_data(UUID projectID, String path, String userId, boolean isAdmin)
            throws UserException, InvalidException, NotAuthorizedException, PathException, IOException {

        if (isInvalidPath(projectID, path))
            throw new PathException("Chemin invalide"); // 400

        if (!isAdmin && !isMember(userId, projectID))
            throw new UserException("L'utilisateur n'a pas les droits"); // 403

        Path basePath = Paths.get("/var/www/projects", projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (isPathTraversal(path, projectID))
            throw new UserException("Path traversal detected"); // 401

        if (!Files.exists(requestedPath))
            throw new InvalidException("Fichier non trouvé"); // 404

        return Files.readAllBytes(requestedPath); // 200
    }

}