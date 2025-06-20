package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.ProjectResponse;
import fr.epita.assistants.ping.api.response.UserInfoResponse;
import fr.epita.assistants.ping.data.converter.UserModelToUserInfoConverter;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.ProjectRepository;
import fr.epita.assistants.ping.utils.Feature;
import fr.epita.assistants.ping.utils.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProjectService {
    @Inject
    ProjectRepository projectRepository;
    @Inject
    UserModelToUserInfoConverter userModelToUserInfoConverter;
    @Inject
    ProjectMembersService projectMembersService;
    @Inject
    UserService userService;

    @ConfigProperty(name="PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;

    public ArrayList<ProjectResponse> buildGetProjectsResponse(String userUUID, boolean onlyOwned) {
        ArrayList<ProjectResponse> responses = new ArrayList<>();

        if (onlyOwned) {
            List<ProjectModel> projects_owned = projectRepository.getOwnedProjects(userService.get(UUID.fromString(userUUID)));
            fillResponses(responses, projects_owned);
        }
        else
        {
            List<ProjectModel> projects_member = projectRepository.getMemberProjects(userUUID);
            fillResponses(responses, projects_member);
        }
        return responses;
    }

    private ArrayList<UserInfoResponse> getMembersInfo(ProjectModel project) {
        ArrayList<UserInfoResponse> members = new ArrayList<>();

        project.members.forEach((member) -> {
            UserModel user = userService.get(member.memberUUID);
            members.add(userModelToUserInfoConverter.convert(user));
        });
        return members;
    }

    private void fillResponses(ArrayList<ProjectResponse> responses, List<ProjectModel> projects) {
        for (ProjectModel project : projects) {
            UserModel owner = project.getOwner();
            UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

            ArrayList<UserInfoResponse> members = getMembersInfo(project);

            responses.add(
                    new ProjectResponse()
                            .withId(project.getId().toString())
                            .withOwner(ownerInfo)
                            .withName(project.getName())
                            .withMembers(members)
                            );
        }
    }

    public ProjectResponse buildGetProjectResponse(ProjectModel project) {

        UserModel owner = project.getOwner();
        UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

        ArrayList<UserInfoResponse> members = getMembersInfo(project);
        return new ProjectResponse()
                        .withId(project.getId().toString())
                        .withOwner(ownerInfo)
                        .withName(project.getName())
                        .withMembers(members);
    }

    private boolean createProjectFolder(ProjectModel createdProject) {
        File projectDir = new File(createdProject.path);
        return projectDir.mkdirs();
    }

    public ProjectResponse buildCreateProjectResponse(String projectName, UserModel user) {
        ProjectModel createdProject = projectRepository.createNewProject(projectName, user);
        createProjectFolder(createdProject);
//        System.out.println("created ? -> " + createProjectFolder(createdProject));

        UserInfoResponse owner = userModelToUserInfoConverter.convert(user);

        ArrayList<UserInfoResponse> members = new ArrayList<>();
        members.add(owner);
        projectMembersService.addMemberToProject(user.getId(), createdProject.id);

        return new ProjectResponse()
                .withId(createdProject.id.toString())
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
        if (newOwnerUUID != null && getUserStatus(newOwnerUUID, projectUUID, false) == UserStatus.NOT_A_MEMBER)
        {
            return null;
        }
        return projectRepository.updateProject(projectUUID, userService.get(newOwnerUUID), newName);
    }

    public ProjectResponse buildGetProjectResponseWithId(UUID projectUUID) {
        ProjectModel projectModel = projectRepository.findProjectByUUID(projectUUID);
        return buildGetProjectResponse(projectModel);
    }

    private boolean deleteProjectFolder(UUID projectUUID) {
        File projectDir = new File(defaultPath + projectUUID);
        return projectDir.delete();
    }

    public void deleteProjectById(UUID projectUUID) {
        projectRepository.deleteProjectById(projectUUID);
        deleteProjectFolder(projectUUID);
    }

    public boolean execFeature(UUID projectId, Feature feature, String command, ArrayList<String> params) {
        ProjectModel projectModel = projectRepository.findProjectByUUID(projectId);
        if (feature == Feature.GIT)
        {
            switch (command) {
                case "init" -> {
                    try (Git git = Git.init().setDirectory(new File(projectModel.path)).call()) {
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                case "add" -> {
                    try (Git git = Git.open(new File(projectModel.path))) {
                        params.forEach(filePattern -> {
                            try {
                                git.add().addFilepattern(filePattern).call();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                case "commit" -> {
                    try (Git git = Git.open(new File(projectModel.path))) {
                        git.commit().setMessage(params.getFirst()).call();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            return false;
        }
        return false;
    }
}
