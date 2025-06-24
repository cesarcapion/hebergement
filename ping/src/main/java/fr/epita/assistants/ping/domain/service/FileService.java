package fr.epita.assistants.ping.domain.service;
import fr.epita.assistants.ping.errors.Exceptions.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;

@ApplicationScoped
public class FileService {
    @ConfigProperty(name= "PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;
    @Inject
    ProjectService projectService;

    private boolean isInvalidPath(UUID projectID, String path)
    {
        return path == null || projectService.DoesNotExist(projectID) || path.isBlank();
    }

    boolean isPathTraversal(String path, UUID projectID)
    {
        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();
        return !requestedPath.startsWith(basePath);
    }


    /*
        return all the data of a file into a byte array
     */
    public byte[] file_data(UUID projectID, String path, String userId, boolean isAdmin)
            throws UserException, InvalidException, PathException, IOException {

        if (isInvalidPath(projectID, path) || path.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !projectService.isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(requestedPath))
            throw new InvalidException("Fichier non trouvé"); // 404


        return Files.readAllBytes(requestedPath); // 200
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
        delete a file/directory
     */
    public void deleteFile(UUID projectID, String userId, String path, boolean isAdmin) throws PathException, UserException, InvalidException {

        if (isInvalidPath(projectID, path))
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !projectService.isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(requestedPath))
            throw new InvalidException("Fichier non trouvé"); // 404

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
        create a file/directory
     */
    public void createFile(UUID projectID, String userId, String path, boolean isAdmin) throws PathException, UserException, InvalidException, AlreadyExistException, IOException {

        if (isInvalidPath(projectID, path) || path.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !projectService.isMember(userId, projectID)) || isPathTraversal(path, projectID))
            throw new UserException("L'utilisateur n'a pas les droits ou path traversal détecté"); // 403

        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();

        if (!Files.exists(basePath)) {
            //System.out.println(basePath);
            throw new InvalidException("Le projet est introuvable"); // 404
        }
        if (Files.exists(requestedPath))
            throw  new AlreadyExistException("le fichier existe deja"); // 409
        Files.createDirectories(requestedPath.getParent());
        Files.createFile(requestedPath);
    }

    /*
        move a file/directory
    */
    public void moveFile(UUID projectID, String userId, String src, String dst, boolean isAdmin) throws PathException, UserException, InvalidException, AlreadyExistException, IOException {
        if (isInvalidPath(projectID, src) || src.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if (isInvalidPath(projectID, dst) || dst.isBlank())
            throw new PathException("Chemin invalide"); // 400

        if ((!isAdmin && !projectService.isMember(userId, projectID)) || isPathTraversal(src, projectID) || isPathTraversal(dst,projectID))
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

    /*
        uplod a file
     */
    public void uploadFile(UUID projectID, String userId, String path, InputStream inputStream, boolean isAdmin) throws PathException, UserException, InvalidException, IOException {
        Path basePath = Paths.get(defaultPath, projectID.toString());
        Path requestedPath = basePath.resolve(path).normalize();
        try {
            createFile(projectID,userId,path,isAdmin);
            try (OutputStream out = Files.newOutputStream(requestedPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                inputStream.transferTo(out);
            }
        }
        catch (AlreadyExistException e)
        {
            try (OutputStream out = Files.newOutputStream(requestedPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                inputStream.transferTo(out);
            }
        }
    }


}