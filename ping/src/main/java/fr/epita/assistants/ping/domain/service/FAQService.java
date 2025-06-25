package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.request.FAQRequest;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.data.model.FAQModel;
import fr.epita.assistants.ping.data.repository.FAQRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FAQService {
    @Inject
    FAQRepository repository;

    public FAQResponse[] getAll()
    {
        List<FAQModel> list = repository.listAll();
        List<FAQResponse> response = new ArrayList<>();
        for (FAQModel model : list) {
            FAQResponse element = new FAQResponse(model.id,model.questions,model.answers);
            response.add(element);
        }
        return response.toArray(new FAQResponse[0]); // 200
    }
    public FAQResponse createQuestion(FAQRequest request)
    {
        FAQModel model = new FAQModel();
        model.setQuestions(request.question);
        model.setAnswers(request.response);
        repository.addFAQ(model);
        return new FAQResponse(model.id,model.questions,model.answers);
    }

}
