package fr.epita.assistants.ping.data.converter;

import fr.epita.assistants.ping.api.response.TicketHistoryResponse;
import fr.epita.assistants.ping.data.model.TicketHistoryModel;
import fr.epita.assistants.ping.utils.IConverter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HistoryModelToHistoryInfoConverter implements IConverter<TicketHistoryModel, TicketHistoryResponse> {

    @Override
    public TicketHistoryResponse convert(TicketHistoryModel ticketHistoryModel) {
        return new TicketHistoryResponse()
                .withId(ticketHistoryModel.getId())
                .withTicketId(ticketHistoryModel.getTicket().getId())
                .withContentPath(ticketHistoryModel.getContentPath())
                .withResourcePath(ticketHistoryModel.getResourcePath())
                .withTicketStatus(ticketHistoryModel.getTicketStatus())
                .withInteractedBy(ticketHistoryModel.getInteractedBy().getId())
                .withInteractedOn(ticketHistoryModel.getInteractedOn());
    }
}
