package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.request.FAQRequest;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.data.model.FAQModel;
import fr.epita.assistants.ping.data.repository.CategoryRepository;
import fr.epita.assistants.ping.data.repository.FAQRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FAQService {
    @Inject
    FAQRepository repository;
    @Inject
    CategoryRepository categoryRepository;
    public FAQResponse[] getAll()
    {
        List<FAQModel> list = repository.listAll();
        List<FAQResponse> response = new ArrayList<>();
        for (FAQModel model : list) {
            FAQResponse element = new FAQResponse(model.getId(),model.getQuestion(),model.getAnswer(), model.getCategory().getId());
            response.add(element);
        }
        return response.toArray(new FAQResponse[0]); // 200
    }

    public FAQResponse createQuestion(FAQRequest request)
    {
        FAQModel model = new FAQModel();
        model.setQuestion(request.question);
        model.setAnswer(request.response);
        if (categoryRepository.getById(request.categoryId) == null)
            throw new NotFoundException("Non trouvé: " + request.categoryId);

        model.setCategory(categoryRepository.getById(request.categoryId));
        repository.addFAQ(model);
        return new FAQResponse(model.getId(),model.getQuestion(),model.getAnswer(),model.getCategory().getId());
    }

    public FAQResponse updateQuestion(FAQRequest request) {
        FAQModel model = repository.findById(request.id);
        if (model == null) {
            throw new NotFoundException("Non trouvé: " + request.getId());
        }

        if (request.question != null) model.setQuestion(request.question);
        if (request.response != null) model.setAnswer(request.response);
        if (request.categoryId != null) {
            if (categoryRepository.getById(request.categoryId) == null) {
                throw new NotFoundException("Non trouvé: " + request.categoryId);
            }
            model.setCategory(categoryRepository.getById(request.categoryId));
        }

        repository.updateFAQ(model.getId(),model);
        return new FAQResponse(model.getId(), model.getQuestion(), model.getAnswer(), model.getCategory().getId());
    }

    public boolean deleteFAQ(Long id) {
        return repository.deleteFAQ(id);
    }
}
