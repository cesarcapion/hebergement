package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.FAQModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FAQRepository implements PanacheRepository<FAQModel> {
    public List<FAQModel> listAll() {
        return findAll().stream().toList();
    }

    @Transactional
    public void addFAQ(FAQModel faq) {
        persist(faq);
    }
    @Transactional
    public void updateFAQ(Long id, FAQModel updatedData) {
        FAQModel existing = findById(id);
        if (existing != null ) {
            existing.setQuestion(updatedData.getQuestion());
            existing.setAnswer(updatedData.getAnswer());
            existing.setCategory(updatedData.getCategory());
        }
        else throw new EntityNotFoundException(FAQModel.class.getName());
    }

    @Transactional
    public boolean deleteFAQ(Long id) {
        FAQModel faq = findById(id);
        if (faq == null) {
            return false;
        }
        delete(faq);
        return true;
    }


}
