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
    public List<TicketHistoryModel> findByTicket(TicketModel ticket) {
        return find("ticket", ticket).list();
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
    public List<TicketHistoryModel> findByUser(UserModel user) {
        return find("interactedBy = ?1 order by interactedOn", user).list();
    }

    public TicketHistoryModel findPrev(TicketModel ticket, LocalDateTime before) {
        return find("ticket = ?1 and interactedOn < ?2 order by interactedOn desc", ticket, before)
                .firstResult();
    }
    public List<TicketHistoryModel> findByMail(String mail, int delay) {
        LocalDateTime since = LocalDateTime.now().minusDays(delay);
        return find("interactedBy.mail = ?1 AND interactedOn >= ?2", mail, since).list();
    }

}
