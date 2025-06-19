package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.ProjectMembersRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class ProjectMembersService {
    @Inject
    ProjectMembersRepository projectMembersRepository;

    public void addMemberToProject(UUID memberUUID, UUID projectUUID) {
        projectMembersRepository.addMemberToProject(memberUUID, projectUUID);
    }

    public void deleteAllMembers(UUID projectUUID) {
        projectMembersRepository.deleteAllMembers(projectUUID);
    }

    /// return true if the user was added to the project, otherwise the user was already present in the project and it returns false
    public boolean addUserToProject(UUID userUUID, UUID projectUUID) {
        // FIXME retrieve the user from the database
        UserModel userToAdd = new UserModel(userUUID, "", "", "", false, "");

        return projectMembersRepository.addUserToProject(userUUID, projectUUID);
    }

    public boolean deleteUserFromProject(UUID userUUID, UUID projectUUID) {
        return projectMembersRepository.deleteUserFromProject(userUUID, projectUUID);
    }

    public void deleteFromAllProjects(UUID userUUID) {
        projectMembersRepository.deleteUserFromAllProjects(userUUID);
    }
}
