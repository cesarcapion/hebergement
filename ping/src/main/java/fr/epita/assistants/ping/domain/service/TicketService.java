package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.TicketResponse;
import fr.epita.assistants.ping.api.response.UserInfoResponse;
import fr.epita.assistants.ping.data.converter.UserModelToUserInfoConverter;
import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.TicketRepository;
import fr.epita.assistants.ping.utils.Feature;
import fr.epita.assistants.ping.utils.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketService {
    @Inject
    TicketRepository ticketRepository;
    @Inject
    UserModelToUserInfoConverter userModelToUserInfoConverter;
    @Inject
    UserService userService;

    @ConfigProperty(name="PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;


    public boolean DoesNotExist(UUID ticketUUID) {
        return ticketRepository.findTicketByUUID(ticketUUID) == null;
    }
    public boolean isMember(String userId, UUID ticketUUID)
    {
        TicketModel ticket = ticketRepository.findTicketByUUID(ticketUUID);
        return ticket.getMembers().stream().filter(userModel -> userModel.getId().equals(UUID.fromString(userId))).count() == 1;
    }

    public ArrayList<TicketResponse> buildGetTicketsResponse(String userUUID, boolean onlyOwned) {
        ArrayList<TicketResponse> responses = new ArrayList<>();

        if (onlyOwned) {
            List<TicketModel> ownedTickets = ticketRepository.getOwnedTickets(userService.get(UUID.fromString(userUUID)));
            fillResponses(responses, ownedTickets);
        }
        else
        {
            List<TicketModel> memberTickets = ticketRepository.getMemberTickets(userUUID);
            fillResponses(responses, memberTickets);
        }
        return responses;
    }

    private ArrayList<UserInfoResponse> getMembersInfo(TicketModel ticket) {
        ArrayList<UserInfoResponse> members = new ArrayList<>();

        ticket.members.forEach((user) -> {
//            UserModel user = userService.get(member.memberUUID);
            members.add(userModelToUserInfoConverter.convert(user));
        });
        return members;
    }

    private void fillResponses(ArrayList<TicketResponse> responses, List<TicketModel> tickets) {
        for (TicketModel ticket : tickets) {
            UserModel owner = ticket.getOwner();
            UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

            ArrayList<UserInfoResponse> members = getMembersInfo(ticket);

            responses.add(
                    new TicketResponse()
                            .withId(ticket.getId().toString())
                            .withOwner(ownerInfo)
                            .withName(ticket.getName())
                            .withMembers(members)
                            );
        }
    }

    public TicketResponse buildGetTicketResponse(TicketModel ticket) {

        UserModel owner = ticket.getOwner();
        UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

        ArrayList<UserInfoResponse> members = getMembersInfo(ticket);
        return new TicketResponse()
                        .withId(ticket.getId().toString())
                        .withOwner(ownerInfo)
                        .withName(ticket.getName())
                        .withMembers(members);
    }

    private boolean createTicketFolder(TicketModel createdTicket) {
        File ticketDir = new File(createdTicket.path);
        return ticketDir.mkdirs();
    }

    public TicketResponse buildCreateTicketResponse(String ticketName, UserModel user) {
        TicketModel createdTicket = ticketRepository.createNewTicket(ticketName, user);
        createTicketFolder(createdTicket);
//        System.out.println("created ? -> " + createProjectFolder(createdProject));

        UserInfoResponse owner = userModelToUserInfoConverter.convert(user);

        ArrayList<UserInfoResponse> members = new ArrayList<>();
        members.add(owner);
//        projectMembersService.addMemberToProject(user.getId(), createdProject.id);

        return new TicketResponse()
                .withId(createdTicket.id.toString())
                .withName(ticketName)
                .withMembers(members)
                .withOwner(owner);
    }

    public ArrayList<TicketResponse> buildGetAllTicketsResponse() {
        List<TicketModel> tickets = ticketRepository.getAllTickets();
        ArrayList<TicketResponse> responses = new ArrayList<>();
        fillResponses(responses, tickets);
        return responses;
    }

    public UserStatus getUserStatus(UserModel user, UUID ticketUUID, boolean isAdmin) {
        return ticketRepository.getUserStatus(user, ticketUUID, isAdmin);
    }

    /// returns the updated project if the new owner is member of the project and updates it, null otherwise
    public TicketModel UpdateTicket(UUID ticketUUID, UserModel newOwner, String newName) {
        if (newOwner != null && getUserStatus(newOwner, ticketUUID, false) == UserStatus.NOT_A_MEMBER)
        {
            return null;
        }
        return ticketRepository.updateTicket(ticketUUID, newOwner, newName);
    }

    public TicketResponse buildGetTicketResponseWithId(UUID ticketUUID) {
        TicketModel ticketModel = ticketRepository.findTicketByUUID(ticketUUID);
        return buildGetTicketResponse(ticketModel);
    }

    private boolean deleteTicketFolder(UUID ticketUUID) {
        TicketModel ticketToDelete = ticketRepository.findTicketByUUID(ticketUUID);
//        File projectDir = new File(projectToDelete.getPath());
//        return projectDir.delete();
        Path path = Path.of(ticketToDelete.getPath());
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path1 ->
                    {
                        try {
                            Files.delete(path1);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /// order matters in this function because the folder needs the project path to be deleted
    public void deleteTicketById(UUID ticketUUID) {
        deleteTicketFolder(ticketUUID);
        ticketRepository.deleteTicketById(ticketUUID);
    }

    public boolean addUserToTicket(UUID ticketUUID, UserModel user) {
        return ticketRepository.addUserToTicket(ticketUUID, user);
    }

    public void deleteUserFromTicket(UUID ticketUUID, UserModel user) {
        ticketRepository.deleteUserFromTicket(ticketUUID, user);
    }

    public void deleteFromAllProjects(UUID userUUID) {
        ticketRepository.deleteFromAllTickets(userService.get(userUUID));
    }

    public boolean execFeature(UUID ticketUUID, Feature feature, String command, ArrayList<String> params) {
        TicketModel ticketModel = ticketRepository.findTicketByUUID(ticketUUID);
        if (feature == Feature.GIT)
        {
            switch (command) {
                case "init" -> {
                    try (Git git = Git.init().setDirectory(new File(ticketModel.path)).call()) {
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                case "add" -> {
                    try (Git git = Git.open(new File(ticketModel.path))) {
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
                    try (Git git = Git.open(new File(ticketModel.path))) {
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
