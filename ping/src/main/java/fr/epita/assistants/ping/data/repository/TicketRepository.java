package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.utils.TicketStatus;
import fr.epita.assistants.ping.utils.UserStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<TicketModel> {
    @ConfigProperty(name= "PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;

    /// returns all the projects owned by userUUID
    public List<TicketModel> getOwnedTickets(UserModel user)
    {
        return find("owner", user).stream().toList();
    }

    /// returns all the projects where there is a member with userUUID
    public List<TicketModel> getMemberTickets(String userUUID)
    {
        // No need to add projects where the owner is this user, because owning a project implies
        // that you are member of it too
        //System.out.println(findAll().firstResult().getMembers());
        return findAll().stream()
                .filter(ticketModel -> ticketModel.getMembers().stream()
                .filter(userModel -> userModel.getId().equals(UUID.fromString(userUUID))).count() == 1)
                .toList();
    }

    @Transactional
    public TicketModel createNewTicket(String projectName, UserModel user)
    {
        String path = defaultPath;
        if (!path.endsWith("/"))
        {
            path += "/";
        }
        TicketModel createdTicket = new TicketModel()
            .withOwner(user)
            .withMembers(new ArrayList<>())
            .withSubject(projectName)
            .withPath("")
            .withTicketStatus(TicketStatus.PENDING)
            .withCreatedAt(LocalDateTime.now());
        persist(createdTicket);

        createdTicket.getMembers().add(user);
        createdTicket.setPath(path + createdTicket.getId());
        return createdTicket;
    }

    public TicketModel findTicketByUUID(UUID ticketUUID)
    {
        return find("id", ticketUUID).firstResult();
    }

    public List<TicketModel> getAllTickets()
    {
        return findAll().stream().toList();
    }

    public UserStatus getUserStatus(UserModel user, UUID ticketUUID, boolean isAdmin)
    {
        TicketModel ticketModel = find("id", ticketUUID).firstResult();
        if (ticketModel == null)
        {
            return UserStatus.ERROR;
        }
        boolean isOwner = ticketModel.owner.getId().equals(user.getId());
        if (isOwner)
        {
            return UserStatus.OWNER;
        }

        boolean isMember = user.getTickets().stream().filter(project -> project.getId().equals(ticketUUID)).count() == 1;
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
    public TicketModel updateTicket(UUID ticketUUID, UserModel newOwner, String newName)
    {
        TicketModel ticketModel = find("id", ticketUUID).firstResult();

        if (newOwner != null && !newOwner.getId().equals(ticketModel.getOwner().getId()))
        {
            ticketModel.setOwner(newOwner);
        }
        if (newName != null && !newName.equals(ticketModel.getSubject()))
        {
            ticketModel.setSubject(newName);
        }

        return ticketModel;
    }

    @Transactional
    public boolean addUserToTicket(UUID ticketUUID, UserModel user)
    {
        TicketModel ticketModel = find("id", ticketUUID).firstResult();
        if (ticketModel.getMembers().stream().filter(userModel -> userModel.getId().equals(user.getId())).count() == 1)
        {
            return false;
        }
        ticketModel.getMembers().add(user);
        return true;
    }

    @Transactional
    public void deleteUserFromTicket(UUID ticketUUID, UserModel user)
    {
        TicketModel ticketModel = find("id", ticketUUID).firstResult();
        //System.out.println("there was " + projectModel.getMembers().size() + " members in project");
        ticketModel.setMembers(ticketModel.getMembers().stream().filter(userModel -> !userModel.getId().equals(user.getId())).collect(Collectors.toList()));
        //System.out.println(projectModel.getMembers().size() + " members in project");
    }

    @Transactional
    public void deleteTicketById(UUID ticketUUID)
    {
        delete("id", ticketUUID);
    }

    @Transactional
    public void deleteFromAllTickets(UserModel user)
    {
        findAll().stream().forEach(ticketModel -> {
            ticketModel.setMembers(ticketModel.getMembers().stream().filter(userModel -> !userModel.getId().equals(user.getId())).collect(Collectors.toList()));
        });
    }
}
