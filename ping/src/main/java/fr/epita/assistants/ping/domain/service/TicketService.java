package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.TicketResponse;
import fr.epita.assistants.ping.api.response.UserInfoResponse;
import fr.epita.assistants.ping.data.converter.TopicModelToTopicInfoConverter;
import fr.epita.assistants.ping.data.converter.UserModelToUserInfoConverter;
import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.TopicModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.TicketRepository;
import fr.epita.assistants.ping.utils.Feature;
import fr.epita.assistants.ping.utils.TicketSortingStrategy;
import fr.epita.assistants.ping.utils.TicketStatus;
import fr.epita.assistants.ping.utils.UserStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.microprofile.config.inject.ConfigProperty;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@ApplicationScoped
public class TicketService {
    @Inject
    TicketRepository ticketRepository;
    @Inject
    UserModelToUserInfoConverter userModelToUserInfoConverter;
    @Inject
    UserService userService;
    @Inject
    TopicModelToTopicInfoConverter topicModelToTopicInfoConverter;

    @ConfigProperty(name="PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;


    public void clear()
    {
        ticketRepository.clear();
    }

    public TicketSortingStrategy validSortingStrategy(String strategy)
    {
        strategy = strategy.toLowerCase();
        if (strategy.equals("last_modified"))
        {
            return TicketSortingStrategy.LAST_MODIFIED;
        }
        if (strategy.equals("status"))
        {
            return TicketSortingStrategy.STATUS;
        }
        return null;
    }



    public boolean ownsProjects(UUID userUUID)
    {
        return !ticketRepository.getOwnedTickets(userService.get(userUUID)).isEmpty();
    }

    public boolean isAdmin(UUID uuid)
    {
        return userService.isAdmin(uuid);
    }
    public TicketStatus getTicketStatus(UUID ticketUUID)
    {
        return ticketRepository.findTicketByUUID(ticketUUID).getTicketStatus();
    }
    public TicketModel get(UUID ticketUUID)
    {
        return ticketRepository.findTicketByUUID(ticketUUID);
    }

    public boolean DoesNotExist(UUID ticketUUID) {
        return ticketRepository.findTicketByUUID(ticketUUID) == null;
    }
    public boolean isMember(String userId, UUID ticketUUID)
    {
        TicketModel ticket = ticketRepository.findTicketByUUID(ticketUUID);
        return ticket.getMembers().stream().filter(userModel -> userModel.getId().equals(UUID.fromString(userId))).count() == 1;
    }

    public ArrayList<TicketResponse> buildGetTicketsResponse(String userUUID, boolean onlyOwned, boolean descending,
                                                             TicketStatus filter, TicketSortingStrategy sortingStrategy) {
        ArrayList<TicketResponse> responses = new ArrayList<>();

        if (onlyOwned) {
            List<TicketModel> ownedTickets = ticketRepository.getOwnedTickets(userService.get(UUID.fromString(userUUID)));
            fillResponses(responses, ownedTickets, filter);
        }
        else
        {
            List<TicketModel> memberTickets = ticketRepository.getMemberTickets(userUUID);
            fillResponses(responses, memberTickets, filter);
        }

        if (sortingStrategy == TicketSortingStrategy.STATUS)
        {
            responses.sort(Comparator.comparing(TicketResponse::getStatus));
        }
        else if (sortingStrategy == TicketSortingStrategy.LAST_MODIFIED)
        {
            responses.sort(Comparator.comparing(TicketResponse::getLastModified).reversed());
        }
        if (descending)
        {
            Collections.reverse(responses);
        }
        return responses;
    }

    private ArrayList<UserInfoResponse> getMembersInfo(TicketModel ticket) {
        ArrayList<UserInfoResponse> members = new ArrayList<>();

        ticket.members.forEach((user) -> {
            members.add(userModelToUserInfoConverter.convert(user));
        });
        return members;
    }

    private void fillResponses(ArrayList<TicketResponse> responses, List<TicketModel> tickets, TicketStatus filter) {
        for (TicketModel ticket : tickets) {
            UserModel owner = ticket.getOwner();
            UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

            ArrayList<UserInfoResponse> members = getMembersInfo(ticket);
            if (filter == TicketStatus.NONE) {
                responses.add(
                        new TicketResponse()
                                .withId(ticket.getId().toString())
                                .withOwner(ownerInfo)
                                .withName(ticket.getSubject())
                                .withMembers(members)
                                .withStatus(ticket.getTicketStatus())
                                .withLastModified(ticket.getCreatedAt())
                                .withTopic(topicModelToTopicInfoConverter.convert(ticket.getTopic()))
                );
            }
            else if (ticket.getTicketStatus() == filter)
            {
                responses.add(
                        new TicketResponse()
                                .withId(ticket.getId().toString())
                                .withOwner(ownerInfo)
                                .withName(ticket.getSubject())
                                .withMembers(members)
                                .withStatus(ticket.getTicketStatus())
                                .withLastModified(ticket.getCreatedAt())
                                .withTopic(topicModelToTopicInfoConverter.convert(ticket.getTopic()))
                );
            }
        }
    }

    public TicketResponse buildGetTicketResponse(TicketModel ticket) {

        UserModel owner = ticket.getOwner();
        UserInfoResponse ownerInfo = userModelToUserInfoConverter.convert(owner);

        ArrayList<UserInfoResponse> members = getMembersInfo(ticket);
        return new TicketResponse()
                .withId(ticket.getId().toString())
                .withOwner(ownerInfo)
                .withName(ticket.getSubject())
                .withMembers(members)
                .withStatus(ticket.getTicketStatus())
                .withLastModified(ticket.getCreatedAt())
                .withTopic(topicModelToTopicInfoConverter.convert(ticket.getTopic()));
    }

    private boolean createTicketFolder(TicketModel createdTicket) {
        File ticketDir = new File(createdTicket.path);
        return ticketDir.mkdirs();
    }

    public TicketResponse buildCreateTicketResponse(String ticketName, UserModel user, TopicModel topic) {
        TicketModel createdTicket = ticketRepository.createNewTicket(ticketName, user, topic);
        createTicketFolder(createdTicket);

        UserInfoResponse owner = userModelToUserInfoConverter.convert(user);

        ArrayList<UserInfoResponse> members = new ArrayList<>();
        members.add(owner);

        return new TicketResponse()
                .withId(createdTicket.id.toString())
                .withName(ticketName)
                .withMembers(members)
                .withOwner(owner)
                .withStatus(createdTicket.getTicketStatus())
                .withLastModified(createdTicket.getCreatedAt())
                .withTopic(topicModelToTopicInfoConverter.convert(topic));
    }

    public ArrayList<TicketResponse> buildGetAllTicketsResponse() {
        List<TicketModel> tickets = ticketRepository.getAllTickets();
        ArrayList<TicketResponse> responses = new ArrayList<>();
        fillResponses(responses, tickets, TicketStatus.NONE);
        return responses;
    }

    public UserStatus getUserStatus(UserModel user, UUID ticketUUID, boolean isAdmin) {
        return ticketRepository.getUserStatus(user, ticketUUID, isAdmin);
    }

    /// returns the updated project if the new owner is member of the project and updates it, null otherwise
    public TicketModel UpdateTicket(UUID ticketUUID, UserModel newOwner, String newSubject, TicketStatus newTicketStatus) {
        if (newOwner != null && getUserStatus(newOwner, ticketUUID, false) == UserStatus.NOT_A_MEMBER)
        {
            return null;
        }
        return ticketRepository.updateTicket(ticketUUID, newOwner, newSubject, newTicketStatus);
    }

    public TicketResponse buildGetTicketResponseWithId(UUID ticketUUID) {
        TicketModel ticketModel = ticketRepository.findTicketByUUID(ticketUUID);
        return buildGetTicketResponse(ticketModel);
    }

    private boolean deleteTicketFolder(UUID ticketUUID) {
        TicketModel ticketToDelete = ticketRepository.findTicketByUUID(ticketUUID);

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
