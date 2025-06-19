package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.ProjectMembersModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class ProjectMembersRepository implements PanacheRepository<ProjectMembersModel> {

    @Transactional
    public void addMemberToProject(UUID memberUUID, UUID projectUUID) {
        persist(new ProjectMembersModel()
                .withProjectUUID(projectUUID)
                .withMemberUUID(memberUUID));
    }

    public ProjectMembersModel findByMemberId(UUID id) {
        return find("memberUUID", id).firstResult();
    }

    public boolean isUserInProject(UUID userUUID, UUID projectUUID) {
        return find("projectUUID = ?1 and memberUUID = ?2", projectUUID, userUUID)
                .firstResult() != null;
    }


    @Transactional
    public void deleteAllMembers(UUID projectUUID)
    {
        delete("projectUUID", projectUUID);
    }

    @Transactional
    public boolean addUserToProject(UUID userUUID, UUID projectUUID)
    {
        ProjectMembersModel memberInProject = find("projectUUID = ?1 and memberUUID = ?2", projectUUID, userUUID).firstResult();
        if (memberInProject != null)
        {
            return false;
        }
        persist(new ProjectMembersModel().withProjectUUID(projectUUID).withMemberUUID(userUUID));
        return true;
    }

    @Transactional
    public boolean deleteUserFromProject(UUID userUUID, UUID projectUUID)
    {
        ProjectMembersModel memberInProject = find("projectUUID = ?1 and memberUUID = ?2", projectUUID, userUUID).firstResult();
        if (memberInProject == null)
        {
            return false;
        }
        delete(memberInProject);
        return true;
    }

    @Transactional
    public void deleteUserFromAllProjects(UUID userUUID)
    {
        find("memberUUID", userUUID).stream().forEach(this::delete);
    }
}
