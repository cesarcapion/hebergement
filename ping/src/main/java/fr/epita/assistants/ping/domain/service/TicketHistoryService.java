package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.TicketHistoryResponse;
import fr.epita.assistants.ping.data.converter.HistoryModelToHistoryInfoConverter;
import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.data.repository.TicketHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}
