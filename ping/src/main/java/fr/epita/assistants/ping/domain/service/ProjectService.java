package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.ProjectResponse;
import fr.epita.assistants.ping.api.response.UserInfoResponse;
import fr.epita.assistants.ping.data.converter.ProjectModelConverter;
import fr.epita.assistants.ping.data.converter.UserModelToUserInfoConverter;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import fr.epita.assistants.ping.domain.entity.ProjectEntity;
import fr.epita.assistants.ping.utils.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProjectService {
    @Inject
    ProjectRepository projectRepository;
    @Inject
    ProjectModelConverter projectModelConverter;
    @Inject
    UserModelToUserInfoConverter userModelToUserInfoConverter;
    @Inject
    ProjectMembersService projectMembersService;

    public ArrayList<ProjectResponse> buildGetProjectsResponse(boolean onlyOwned) {
        ArrayList<ProjectResponse> responses = new ArrayList<>();

        if (onlyOwned) {
            List<ProjectModel> projects_owned = projectRepository.getOwnedProjects(""); // FIXME place userUUID in the parameter when user implemented
            fillResponses(responses, projects_owned);
        }
        else
        {
            List<ProjectModel> projects_member = projectRepository.getMemberProjects(""); // FIXME place userUUID in the parameter when user implemented
            fillResponses(responses, projects_member);
        }
        return responses;
    }

    private void fillResponses(ArrayList<ProjectResponse> responses, List<ProjectModel> projects) {
        for (ProjectModel project : projects) {
            ProjectEntity projectEntity = projectModelConverter.convert(project);
            ArrayList<UserInfoResponse> members = new ArrayList<>();
            UserInfoResponse owner = new UserInfoResponse("", "", "");
            // FIXME find the user with the member.memberUUID and convert it to a UserInfo, then set it above
            project.members.forEach((member) -> {
                // FIXME find the user with the member.memberUUID and convert it to a UserInfo, then add it on the members list
            });
            responses.add(
                    new ProjectResponse()
                            .withId(projectEntity.project_id)
                            .withOwner(owner)
                            .withName(project.name)
                            .withMembers(members)
                            );
        }
    }

    public ProjectResponse buildGetProjectResponse(ProjectModel project) {
        ProjectEntity projectEntity = projectModelConverter.convert(project);
        ArrayList<UserInfoResponse> members = new ArrayList<>();
        UserInfoResponse owner = new UserInfoResponse(project.ownerId.toString(), "", "");
        // FIXME find the user with the member.memberUUID and convert it to a UserInfo, then set it above
        project.members.forEach((member) -> {
            members.add(new UserInfoResponse(member.memberUUID.toString(), "", ""));
            // FIXME find the user with the member.memberUUID and convert it to a UserInfo, then add it on the members list
        });
        return new ProjectResponse()
                        .withId(projectEntity.project_id)
                        .withOwner(owner)
                        .withName(project.name)
                        .withMembers(members);
    }

    public ProjectResponse buildCreateProjectResponse(String projectName, UserModel user) {
        ProjectModel createdProject = projectRepository.createNewProject(projectName, user);
        UserInfoResponse owner = userModelToUserInfoConverter.convert(user);

        ArrayList<UserInfoResponse> members = new ArrayList<>();
        members.add(owner);
        projectMembersService.addMemberToProject(user.getUuid(), createdProject.uuid);

        return new ProjectResponse()
                .withId(createdProject.uuid.toString())
                .withName(projectName)
                .withMembers(members)
                .withOwner(owner);
    }

    public ArrayList<ProjectResponse> buildGetAllProjectsResponse() {
        List<ProjectModel> projects = projectRepository.getAllProjects();
        ArrayList<ProjectResponse> responses = new ArrayList<>();
        fillResponses(responses, projects);
        return responses;
    }

    public UserStatus getUserStatus(UUID userUUID, UUID projectUUID, boolean isAdmin) {
        return projectRepository.getUserStatus(userUUID, projectUUID, isAdmin);
    }

    /// returns the updated project if the new owner is member of the project and updates it, null otherwise
    public ProjectModel UpdateProject(UUID projectUUID, UUID newOwnerUUID, String newName) {
        if (getUserStatus(newOwnerUUID, projectUUID, false) == UserStatus.NOT_A_MEMBER)
        {
            return null;
        }
        return projectRepository.updateProject(projectUUID, newOwnerUUID, newName);
    }

    public ProjectResponse buildGetProjectResponseWithId(UUID projectUUID) {
        ProjectModel projectModel = projectRepository.findProjectByUUID(projectUUID);
        return buildGetProjectResponse(projectModel);
    }

    public void deleteProjectById(UUID projectUUID) {
        projectRepository.deleteProjectById(projectUUID);
    }

}
