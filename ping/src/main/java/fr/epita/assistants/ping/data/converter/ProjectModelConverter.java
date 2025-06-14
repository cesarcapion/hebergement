package fr.epita.assistants.ping.data.converter;

import fr.epita.assistants.ping.data.model.ProjectMembersModel;
import fr.epita.assistants.ping.data.model.ProjectModel;
import fr.epita.assistants.ping.domain.entity.ProjectEntity;
import fr.epita.assistants.ping.utils.IConverter;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;

@ApplicationScoped
public class ProjectModelConverter implements IConverter<ProjectModel, ProjectEntity> {
    @Override
    public ProjectEntity convert(ProjectModel projectModel) {
        return new ProjectEntity(projectModel.uuid.toString(), projectModel.ownerId, "", "", new ArrayList<>());
        // FIXME fill avatar and displayName and avatar by using UserModel
        // FIXME same for the memberlist use the UserModel
    }
}
