package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.RoleModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RoleRepository implements PanacheRepository<RoleModel> {
    public RoleModel getRoleById(Long id) {
        return find("id", id).firstResult();
    }

    public boolean RoleSameNameExists(String name) {
        return find("name", name).firstResult() != null;
    }

    @Transactional
    public RoleModel createRole(String name) {
        RoleModel role = new RoleModel()
                .withName(name);
        persist(role);
        return role;
    }

    @Transactional
    public void updateRole(Long id, String name) {
        RoleModel role = getRoleById(id);
        role.setName(name);
    }

    public List<RoleModel> getAllRoles() {
        return findAll().stream().toList();
    }

    @Transactional
    public void deleteRoleById(Long id) {
        delete("id", id);
    }
}
