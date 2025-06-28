package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.OneStatResponse;
import fr.epita.assistants.ping.api.response.TicketHistoryResponse;
import fr.epita.assistants.ping.data.converter.HistoryModelToHistoryInfoConverter;
import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.TicketHistoryRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.utils.TicketStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class TicketHistoryService {
    @Inject
    TicketService ticketService;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    TicketHistoryRepository ticketHistoryRepository;

    @Inject
    HistoryModelToHistoryInfoConverter modelToInfoConverter;

    public boolean addHistory(String contentPath, String resourcePath, UUID ticketUUID, UUID userUUId)
    {
        if ((resourcePath != null && !Files.exists(Paths.get(resourcePath)))
                || !Files.exists(Paths.get(contentPath)))
        {
            return false;
        }
        ticketHistoryRepository.addHistory(ticketService.get(ticketUUID), userService.get(userUUId), contentPath, resourcePath);
        return true;
    }

    public List<TicketHistoryResponse> getHistory(UUID ticketUUID)
    {
        List<TicketHistoryModel> ticketHistoryModels = ticketHistoryRepository.findByTicketId(ticketUUID);
        List<TicketHistoryResponse> ticketHistoryResponses = new ArrayList<>();
        ticketHistoryModels.forEach(ticketHistoryModel -> {
            ticketHistoryResponses.add(modelToInfoConverter.convert(ticketHistoryModel));
        });

        return ticketHistoryResponses;
    }
    public long countLatest(List<TicketHistoryModel> ticketHistories,TicketStatus status) {
        Map<UUID, TicketHistoryModel> latestByTicket = new HashMap<>();

        for (TicketHistoryModel history : ticketHistories) {
            UUID ticketId = history.getTicket().getId();
            TicketHistoryModel currentLatest = latestByTicket.get(ticketId);

            if (currentLatest == null || history.getInteractedOn().isAfter(currentLatest.getInteractedOn())) {
                latestByTicket.put(ticketId, history);
            }
        }

        return latestByTicket.values().stream()
                .filter(history -> history.getTicketStatus() == status).count();
    }
    public Duration getAverageResponseTime(String userEmail) {
        UserModel user = userRepository.findByLogin(userEmail);

        List<TicketHistoryModel> userHistories = ticketHistoryRepository
                .findByUser(user);

        List<Duration> durations = new ArrayList<>();

        for (TicketHistoryModel current : userHistories) {
            TicketHistoryModel previousInteraction = ticketHistoryRepository
                    .findPrev(
                            current.getTicket(),
                            current.getInteractedOn()
                    );
                Duration duration = Duration.between(previousInteraction.getInteractedOn(), current.getInteractedOn());
                durations.add(duration);
        }

        if (durations.isEmpty()) return Duration.ZERO;

        long totalSeconds = durations.stream()
                .mapToLong(Duration::getSeconds)
                .sum();

        return Duration.ofSeconds(totalSeconds / durations.size());
    }


    public OneStatResponse getStat(String mail)
    {
        List<TicketHistoryModel> ticketHistoryModels = ticketHistoryRepository.findByMail(mail);
        long resolvedTickets = countLatest(ticketHistoryModels,TicketStatus.RESOLVED);
        long inProgressTickets = countLatest(ticketHistoryModels,TicketStatus.IN_PROGRESS);
        long pendingTickets = ticketService.countPendingTickets();
        Duration averageResponseTime = getAverageResponseTime(mail);
        return new OneStatResponse(resolvedTickets,inProgressTickets,pendingTickets,averageResponseTime);
    }
}
