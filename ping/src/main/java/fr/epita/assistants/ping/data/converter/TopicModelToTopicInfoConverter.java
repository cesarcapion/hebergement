package fr.epita.assistants.ping.data.converter;

import fr.epita.assistants.ping.api.response.TopicInfoResponse;
import fr.epita.assistants.ping.data.model.TopicModel;
import fr.epita.assistants.ping.utils.IConverter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TopicModelToTopicInfoConverter implements IConverter<TopicModel, TopicInfoResponse> {

    @Override
    public TopicInfoResponse convert(TopicModel topicModel) {
        return new TopicInfoResponse().withId(topicModel.getId()).withName(topicModel.getName());
    }
}
