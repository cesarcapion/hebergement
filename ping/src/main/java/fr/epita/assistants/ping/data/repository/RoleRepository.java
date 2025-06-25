package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.RoleModel;
import jakarta.enterprise.context.ApplicationScoped;
import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.utils.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

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

    public RoleModel findByName(String name) {
        return find("LOWER(name)", name.toLowerCase()).firstResult();
        }
    public List<RoleModel> getAllRoles() {
        return findAll().stream().toList();
    }

    @Transactional
    public void deleteRoleById(Long id) {
        delete("id", id);
    }
}
