package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.OneStatResponse;
import fr.epita.assistants.ping.api.response.TicketHistoryResponse;
import fr.epita.assistants.ping.data.converter.HistoryModelToHistoryInfoConverter;
import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.data.repository.TicketHistoryRepository;
import fr.epita.assistants.ping.data.repository.TicketRepository;
import fr.epita.assistants.ping.data.repository.UserRepository;
import fr.epita.assistants.ping.utils.TicketStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Files;
import java.nio.file.Path;
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
    TicketRepository  ticketRepository;
    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    TicketHistoryRepository ticketHistoryRepository;

    @Inject
    HistoryModelToHistoryInfoConverter modelToInfoConverter;
    @ConfigProperty(name= "PROJECT_DEFAULT_PATH", defaultValue = "/tmp/www/projects/") String defaultPath;

    public boolean addHistory(String contentPath, String resourcePath, UUID ticketUUID, UUID userUUId)
    {
        Path basePath = Paths.get(defaultPath, ticketUUID.toString());
        Path contentFullPath = basePath.resolve(contentPath).normalize();
        if (!Files.exists(contentFullPath))
        {
            return false;
        }
        if (resourcePath != null)
        {
            Path resourceFullPath = basePath.resolve(resourcePath).normalize();
            if (!Files.exists(resourceFullPath))
            {
                return false;
            }
        }
        ticketHistoryRepository.addHistory(ticketService.get(ticketUUID), userService.get(userUUId), contentPath, resourcePath);
        return true;
    }

    public List<TicketHistoryResponse> getHistory(UUID ticketUUID)
    {
        List<TicketHistoryModel> ticketHistoryModels = ticketHistoryRepository.findByTicket(ticketRepository.findTicketByUUID(ticketUUID));
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

    public String durationToString(Duration duration) {
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0 || sb.isEmpty()) sb.append(minutes).append("min");

        return sb.toString();
    }

    public OneStatResponse[] getStats( int delay) {
        List<String> mails = userRepository.findAllNonUserMails();
        List<OneStatResponse> oneStatResponses = new ArrayList<>();
        mails.forEach(mail -> {oneStatResponses.add(getStat(mail,delay));});
        return oneStatResponses.toArray(new OneStatResponse[0]);
    }

    public OneStatResponse getStat(String mail, int delay)
    {
        List<TicketHistoryModel> ticketHistoryModels = ticketHistoryRepository.findByMail(mail, delay);
        long resolvedTickets = countLatest(ticketHistoryModels,TicketStatus.RESOLVED);
        long inProgressTickets = countLatest(ticketHistoryModels,TicketStatus.IN_PROGRESS);
        long pendingTickets = ticketService.countPendingTickets();
        Duration averageResponseTime = getAverageResponseTime(mail);
        return new OneStatResponse(mail,pendingTickets,resolvedTickets,inProgressTickets,durationToString(averageResponseTime));
    }
}
