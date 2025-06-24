package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.RoleResponse;
import fr.epita.assistants.ping.data.model.RoleModel;
import fr.epita.assistants.ping.data.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RoleService {
    @Inject
    private RoleRepository roleRepository;

    private String formatName(String name){
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    public boolean roleSameNameExists(String name)
    {
        return roleRepository.RoleSameNameExists(formatName(name));
    }

    public RoleResponse buildCreateRoleResponse(String name)
    {
        RoleModel createdRole = roleRepository.createRole(formatName(name));

        return new RoleResponse().withId(createdRole.getId()).withName(createdRole.getName());
    }

    public boolean roleExists(Long id)
    {
        return roleRepository.getRoleById(id) != null;
    }

    public void updateRole(Long id, String name)
    {
        roleRepository.updateRole(id, formatName(name));
    }

    public RoleResponse buildGetProjectResponse(Long id)
    {
        RoleModel roleModel = roleRepository.getRoleById(id);
        return new RoleResponse().withId(roleModel.getId()).withName(roleModel.getName());
    }

    public List<RoleResponse> buildGetAllRolesResponse()
    {
        List<RoleResponse> rolesResponses = new ArrayList<>();
        List<RoleModel> roles = roleRepository.getAllRoles();
        roles.forEach(role -> rolesResponses.add(new RoleResponse().withId(role.getId()).withName(role.getName())));
        return rolesResponses;
    }

    public void deleteRoleById(Long id)
    {
        // FIXME shouldn't allow removing role if there is still topics linked to it
        roleRepository.deleteRoleById(id);
    }
}
