package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.data.model.TicketModel;
import fr.epita.assistants.ping.data.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@ApplicationScoped
public class TicketHistoryRepository implements PanacheRepository<TicketHistoryModel> {
    public List<TicketHistoryModel> findByTicketId(UUID ticketId)
    {
        return find("ticketId", ticketId).list();
    }

    @Transactional
    public void addHistory(TicketModel ticketModel, UserModel userModel, String contentPath, String resourcePath)
    {
        TicketHistoryModel ticketHistoryModel = new TicketHistoryModel()
                .withTicket(ticketModel)
                .withInteractedBy(userModel)
                .withContentPath(contentPath)
                .withResourcePath(resourcePath)
                .withTicketStatus(ticketModel.getTicketStatus())
                .withInteractedOn(LocalDateTime.now());
        persist(ticketHistoryModel);
    }
}
