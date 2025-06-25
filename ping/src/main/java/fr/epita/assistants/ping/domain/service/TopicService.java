package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.response.TopicInfoResponse;
import fr.epita.assistants.ping.data.converter.TopicModelToTopicInfoConverter;
import fr.epita.assistants.ping.data.model.TopicModel;
import fr.epita.assistants.ping.data.repository.TopicRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TopicService {
    @Inject
    TopicRepository topicRepository;
    @Inject
    TopicModelToTopicInfoConverter topicModelToTopicInfoConverter;

    private String formatName(String name){
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    public boolean topicSameNameExists(String name){
        return topicRepository.TopicSameNameExists(formatName(name));
    }

    public TopicInfoResponse buildCreateTopicResponse(String name)
    {
        TopicModel topicModel = topicRepository.createTopic(formatName(name));
        return topicModelToTopicInfoConverter.convert(topicModel);
    }

    public TopicInfoResponse buildGetTopicResponse(Long id)
    {
        TopicModel topicModel = topicRepository.getTopicById(id);
        return topicModelToTopicInfoConverter.convert(topicModel);
    }

    public List<TopicInfoResponse> buildGetAllTopicsResponse()
    {
        List<TopicInfoResponse> topicsInfo = new ArrayList<>();
        List<TopicModel> topicModels = topicRepository.getAllTopics();
        topicModels.forEach(topicModel -> {
            topicsInfo.add(topicModelToTopicInfoConverter.convert(topicModel));
        });
        return topicsInfo;
    }

    public boolean topicExists(Long id)
    {
        return topicRepository.getTopicById(id) != null;
    }

    public boolean updateTopic(Long id, String newName)
    {
        if (topicSameNameExists(formatName(newName)))
        {
            return false;
        }
        topicRepository.updateTopic(id, formatName(newName));
        return true;
    }

    public void deleteTopic(Long id)
    {
        topicRepository.deleteTopic(id);
    }

    public TopicModel getTopicById(Long id)
    {
        return topicRepository.getTopicById(id);
    }
}
