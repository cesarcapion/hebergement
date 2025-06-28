package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.OneStatResponse;
import fr.epita.assistants.ping.api.response.TicketHistoryResponse;
import fr.epita.assistants.ping.data.converter.HistoryModelToHistoryInfoConverter;
import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.data.repository.TicketHistoryRepository;
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
    public Duration getAverageResponseTime(String userEmail, List<TicketHistoryModel> allHistories) {
        // On filtre les réponses de cet utilisateur
        List<TicketHistoryModel> userResponses = allHistories.stream()
                .filter(h -> h.getInteractedBy() != null &&
                        userEmail.equals(h.getInteractedBy().getMail()))
                .sorted(Comparator.comparing(TicketHistoryModel::getInteractedOn))
                .toList();

        if (userResponses.isEmpty()) {
            return Duration.ZERO;
        }

        List<Duration> responseTimes = new ArrayList<>();

        // Regrouper toutes les interactions par ticket
        Map<UUID, List<TicketHistoryModel>> historyByTicket = allHistories.stream()
                .filter(h -> h.getTicket() != null && h.getInteractedOn() != null)
                .collect(Collectors.groupingBy(h -> h.getTicket().getId()));

        for (TicketHistoryModel userResponse : userResponses) {
            UUID ticketId = userResponse.getTicket().getId();
            LocalDateTime userTime = userResponse.getInteractedOn();

            // Obtenir les interactions précédentes sur le même ticket
            List<TicketHistoryModel> ticketHistories = historyByTicket.getOrDefault(ticketId, Collections.emptyList());

            // Chercher la dernière interaction avant celle de l'utilisateur
            Optional<LocalDateTime> lastBefore = ticketHistories.stream()
                    .filter(h -> h.getInteractedOn().isBefore(userTime))
                    .map(TicketHistoryModel::getInteractedOn)
                    .max(LocalDateTime::compareTo);

            lastBefore.ifPresent(previousTime -> {
                Duration diff = Duration.between(previousTime, userTime);
                responseTimes.add(diff);
            });
        }

        if (responseTimes.isEmpty()) return Duration.ZERO;

        // Moyenne
        long averageSeconds = responseTimes.stream()
                .mapToLong(Duration::getSeconds)
                .sum() / responseTimes.size();

        return Duration.ofSeconds(averageSeconds);
    }

    public OneStatResponse getStat(String mail)
    {
        List<TicketHistoryModel> ticketHistoryModels = ticketHistoryRepository.findByMail(mail);
        long resolvedTickets = countLatest(ticketHistoryModels,TicketStatus.RESOLVED);
        long inProgressTickets = countLatest(ticketHistoryModels,TicketStatus.IN_PROGRESS);
        long pendingTickets = ticketService.countPendingTickets();
        Duration averageResponseTime = getAverageResponseTime(mail, ticketHistoryModels);
        OneStatResponse oneStatResponses;


        return ticketHistoryResponses;
    }
}
