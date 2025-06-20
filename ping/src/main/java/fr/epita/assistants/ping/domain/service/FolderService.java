package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.GetFolderResponse;
import fr.epita.assistants.ping.data.repository.ProjectMembersRepository;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import fr.epita.assistants.ping.errors.Exceptions.AlreadyExistException;
import fr.epita.assistants.ping.errors.Exceptions.InvalidException;
import fr.epita.assistants.ping.errors.Exceptions.PathException;
import fr.epita.assistants.ping.errors.Exceptions.UserException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FolderService {

    @ConfigProperty(name= "PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;
    @Inject
    ProjectRepository projectRepo;
    @Inject
    ProjectMembersRepository pmRepository;
    private boolean isMember(String userId, UUID projectID)
    {
        return pmRepository.isUserInProject(UUID.fromString(userId),projectID);
    }

    private boolean isInvalidPath(UUID projectID, String path)
    {
        return path == null || projectRepo.findProjectByUUID(projectID) == null;
    }

    boolean isPathTraversal(String path, UUID projectID)
    {
        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();
        return !requestedPath.startsWith(basePath);
    }

    /*
        return all the data of a folder into a byte array
     */
    public GetFolderResponse[] folder_data(UUID projectID, String path, String userId, boolean isAdmin)
            throws UserException, InvalidException {

        if ((!isAdmin && !isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(requestedPath))
            throw new InvalidException("Fichier non trouvé"); // 404


        File directory = requestedPath.toFile();
        File[] allContents = directory.listFiles();
        List<GetFolderResponse> response = new ArrayList<>();
        if (allContents != null) {
            for (File file : allContents) {
                GetFolderResponse element = new GetFolderResponse(file.getName(),basePath.relativize(file.toPath()).toString(),file.isDirectory());

                response.add(element);
            }
        }
        return response.toArray(new GetFolderResponse[0]); // 200
    }

    /*
        delete a directory and all files/directories into it
     */
    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    /*
        delete a directory
     */
    public void deleteFolder(UUID projectID, String userId, String path, boolean isAdmin) throws PathException, UserException, InvalidException {

        if (isInvalidPath(projectID, path))
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(requestedPath) || !Files.isDirectory(requestedPath))
            throw new InvalidException("Dossier non trouvé"); // 404

        if (basePath.equals(requestedPath)) {
            File[] directory = requestedPath.toFile().listFiles();
            if (directory == null)
                return;
            Arrays.stream(directory).forEach(this::deleteDirectory);
        }
        else
        {
            File file = requestedPath.toFile();
            deleteDirectory(file);
        }
    }

    /*
        create a directory
     */
    public void createFolder(UUID projectID, String userId, String path, boolean isAdmin) throws PathException, UserException, InvalidException, AlreadyExistException, IOException {

        if (isInvalidPath(projectID, path) || path.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(basePath))
            throw new InvalidException("Le projet est introuvable"); // 404

        if (Files.exists(requestedPath))
            throw  new AlreadyExistException("le fichier existe deja"); // 409
        Files.createDirectories(requestedPath);
    }

    /*
        move a directory
    */
    public void moveFolder(UUID projectID, String userId, String src, String dst, boolean isAdmin) throws PathException, UserException, InvalidException, AlreadyExistException, IOException {
        if (isInvalidPath(projectID, src) || src.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if (isInvalidPath(projectID, dst) || dst.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !isMember(userId, projectID)) || isPathTraversal(src, projectID) || isPathTraversal(dst,projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path srcRequestedPath = basePath.resolve(src).normalize();
        Path dstRequestedPath = basePath.resolve(dst).normalize();

        if (!Files.exists(basePath))
            throw new InvalidException("Le projet est introuvable"); // 404

        if (!Files.exists(srcRequestedPath)) {
            throw new InvalidException("Le fichier source est introuvable"); // 404
        }

        if (Files.exists(dstRequestedPath))
            throw  new AlreadyExistException("le fichier existe deja"); // 409

        Files.createDirectories(dstRequestedPath.getParent());
        Files.move(srcRequestedPath, dstRequestedPath);
    }
}