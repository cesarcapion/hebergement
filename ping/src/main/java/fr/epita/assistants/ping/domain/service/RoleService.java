package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.RoleResponse;
import fr.epita.assistants.ping.api.response.TopicInfoResponse;
import fr.epita.assistants.ping.data.converter.TopicModelToTopicInfoConverter;
import fr.epita.assistants.ping.data.model.RoleModel;
import fr.epita.assistants.ping.data.model.TopicModel;
import fr.epita.assistants.ping.data.repository.RoleRepository;
import fr.epita.assistants.ping.data.repository.TopicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RoleService {
    @Inject
    RoleRepository roleRepository;
    @Inject
    TopicService topicService;
    @Inject
    TopicModelToTopicInfoConverter topicModelToTopicInfoConverter;


    public void clear() {
        roleRepository.clear();
    }

    private String formatName(String name) {
//        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
        return name;
    }

    public boolean roleSameNameExists(String name) {
        return roleRepository.RoleSameNameExists(formatName(name));
    }

    public RoleResponse buildCreateRoleResponse(String name) {
        RoleModel createdRole = roleRepository.createRole(formatName(name));

        return new RoleResponse().withId(createdRole.getId()).withName(createdRole.getName()).withTopics(new ArrayList<>());
    }

    public RoleModel getRoleById(Long id)
    {
        return roleRepository.getRoleById(id);
    }

    public boolean roleExists(Long id)
    {
        return roleRepository.getRoleById(id) != null;
    }

    public boolean updateRole(Long id, String name)
    {
        if (roleSameNameExists(formatName(name)))
        {
            return false;
        }
        roleRepository.updateRole(id, formatName(name));
        return true;
    }

    public RoleResponse buildGetProjectResponse(Long id)
    {
        RoleModel roleModel = roleRepository.getRoleById(id);
        List<TopicInfoResponse> topics = new ArrayList<>();
        roleModel.getTopics().forEach(topic -> topics.add(topicModelToTopicInfoConverter.convert(topic)));
        return new RoleResponse().withId(roleModel.getId()).withName(roleModel.getName()).withTopics(topics);
    }

    public List<RoleResponse> buildGetAllRolesResponse()
    {
        List<RoleResponse> rolesResponses = new ArrayList<>();
        List<RoleModel> roles = roleRepository.getAllRoles();
        roles.forEach(role -> {
            List<TopicInfoResponse> topics = new ArrayList<>();
            role.getTopics().forEach(topic -> topics.add(topicModelToTopicInfoConverter.convert(topic)));

            rolesResponses.add(new RoleResponse().withId(role.getId()).withName(role.getName()).withTopics(topics));
        });
        return rolesResponses;
    }

    public void deleteRoleById(Long id)
    {
        // FIXME delete all topics related to this role, only if they are only related to this role, otherwise keep them
        roleRepository.deleteRoleById(id);
    }


    public boolean deleteTopicFromRole(Long id, Long topicId)
    {
        return roleRepository.deleteTopicFromRole(id, topicId);
    }

    public boolean addTopicToRole(Long id, Long topicId)
    {
        return roleRepository.addTopicToRole(id, topicService.getTopicById(topicId));
    }

    public RoleModel findByName(String name) {
        return roleRepository.findByName(name);
    }
}
