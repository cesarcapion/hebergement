package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.utils.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectRepository implements PanacheRepository<ProjectModel> {
    @ConfigProperty(name= "PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;

    /// returns all the projects owned by userUUID
    public List<ProjectModel> getOwnedProjects(UserModel user)
    {
        return find("owner", user).stream().toList();
    }

    /// returns all the projects where there is a member with userUUID
    public List<ProjectModel> getMemberProjects(String userUUID)
    {
        // No need to add projects where the owner is this user, because owning a project implies
        // that you are member of it too
        return findAll().stream()
                .filter(projectModel -> projectModel.getMembers().stream()
                .filter(userModel -> userModel.getId().equals(UUID.fromString(userUUID))).count() == 1)
                .toList();
    }

    @Transactional
    public ProjectModel createNewProject(String projectName, UserModel user)
    {
        String path = defaultPath;
        if (!path.endsWith("/"))
        {
            path += "/";
        }
        ProjectModel createdProject = new ProjectModel()
            .withOwner(user)
            .withMembers(new ArrayList<>())
            .withName(projectName)
            .withPath("");
        persist(createdProject);

        createdProject.getMembers().add(user);
        createdProject.setPath(path + createdProject.getId());
        return createdProject;
    }

    public ProjectModel findProjectByUUID(UUID projectUUID)
    {
        return find("id", projectUUID).firstResult();
    }

    public List<ProjectModel> getAllProjects()
    {
        return findAll().stream().toList();
    }

    public UserStatus getUserStatus(UserModel user, UUID projectUUID, boolean isAdmin)
    {
        ProjectModel projectModel = find("id", projectUUID).firstResult();
        if (projectModel == null)
        {
            return UserStatus.ERROR;
        }
        boolean isOwner = projectModel.owner.getId().equals(user.getId());
        if (isOwner)
        {
            return UserStatus.OWNER;
        }

        boolean isMember = user.getProjects().stream().filter(project -> project.getId().equals(projectUUID)).count() == 1;
        if (isMember)
        {
            return UserStatus.MEMBER;
        }
        else
        {
            return UserStatus.NOT_A_MEMBER;
        }
    }

    @Transactional
    public ProjectModel updateProject(UUID projectUUID, UserModel newOwner, String newName)
    {
        ProjectModel projectModel = find("id", projectUUID).firstResult();

        if (newOwner != null && !newOwner.getId().equals(projectModel.getOwner().getId()))
        {
            projectModel.setOwner(newOwner);
        }
        if (newName != null && !newName.equals(projectModel.getName()))
        {
            projectModel.setName(newName);
        }
        return projectModel;
    }

    @Transactional
    public boolean addUserToProject(UUID projectUUID, UserModel user)
    {
        ProjectModel projectModel = find("id", projectUUID).firstResult();
        if (projectModel.getMembers().stream().filter(userModel -> userModel.getId().equals(user.getId())).count() == 1)
        {
            return false;
        }
        projectModel.getMembers().add(user);
        return true;
    }

    @Transactional
    public void deleteUserFromProject(UUID projectUUID, UserModel user)
    {
        ProjectModel projectModel = find("id", projectUUID).firstResult();
        projectModel.setMembers(projectModel.getMembers().stream().filter(userModel -> !userModel.getId().equals(user.getId())).collect(Collectors.toList()));
    }

    @Transactional
    public void deleteProjectById(UUID projectUUID)
    {
        delete("id", projectUUID);
    }

    @Transactional
    public void deleteFromAllProjects(UserModel user)
    {
        findAll().stream().forEach(projectModel -> {
            projectModel.setMembers(projectModel.getMembers().stream().filter(userModel -> !userModel.getId().equals(user.getId())).collect(Collectors.toList()));
        });
    }
}
