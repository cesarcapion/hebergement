package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.utils.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.*;

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
                .filter(projectModel -> projectModel.members.stream()
                .filter(projectMembersModel -> projectMembersModel.memberUUID.equals(UUID.fromString(userUUID))).count() == 1)
                .toList();
    }

    @Transactional
    public ProjectModel createNewProject(String projectName, UserModel user)
    {
//        List<ProjectMembersModel> members = new ArrayList<>();
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
        createdProject.setPath(path + createdProject.getId());
//        members.add(new ProjectMembersModel()
//                .withProjectUUID(projectUUID)
//                .withMemberUUID(user.getId()));
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

    public UserStatus getUserStatus(UUID userUUID, UUID projectUUID, boolean isAdmin)
    {
        ProjectModel projectModel = find("id", projectUUID).firstResult();
        if (projectModel == null)
        {
            return UserStatus.ERROR;
        }
        boolean isOwner = projectModel.owner.getId().equals(userUUID);
        if (isOwner)
        {
            return UserStatus.OWNER;
        }
        boolean isMember = projectModel.members.stream()
                .filter(member -> member.memberUUID.equals(userUUID))
                .count() == 1;
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
    public void deleteProjectById(UUID projectUUID)
    {
        delete("id", projectUUID);
    }

}
