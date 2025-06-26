package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.TopicModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class TopicRepository implements PanacheRepository<TopicModel> {
    public TopicModel getTopicById(Long id) {
        return find("id", id).firstResult();
    }

    public boolean TopicSameNameExists(String name) {
        return find("name", name).firstResult() != null;
    }

    @Transactional
    public TopicModel createTopic(String name) {
        TopicModel topic = new TopicModel().withName(name);
        persist(topic);
        return topic;
    }

    public List<TopicModel> getAllTopics()
    {
        return findAll().stream().toList();
    }

    @Transactional
    public void updateTopic(Long id, String newName)
    {
        TopicModel topic = getTopicById(id);
        topic.setName(newName);
    }

    @Transactional
    public void deleteTopic(Long id) {
        TopicModel topic = getTopicById(id);
        delete(topic);
    }
}
