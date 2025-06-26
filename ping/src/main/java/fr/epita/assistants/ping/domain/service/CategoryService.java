package fr.epita.assistants.ping.domain.service;

import fr.epita.assistants.ping.api.request.FAQRequest;
import fr.epita.assistants.ping.api.response.CategoryResponse;
import fr.epita.assistants.ping.api.response.FAQResponse;
import fr.epita.assistants.ping.api.response.UserResponse;
import fr.epita.assistants.ping.data.model.CategoryModel;
import fr.epita.assistants.ping.data.model.FAQModel;
import fr.epita.assistants.ping.data.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CategoryService {
    @Inject
    CategoryRepository categoryRepository;
    public CategoryModel getById(Long id) {
        return categoryRepository.getById(id);
    }
    public CategoryResponse[] listAllCategories() {
        var response =  categoryRepository.listAll();
        List<CategoryResponse> responses = new ArrayList<>();
        response.forEach(category -> {
            /*var questions = category.getQuestions();
            List<FAQResponse> listResponses = new ArrayList<>();
            questions.forEach(question -> {
                FAQResponse faq = new FAQResponse(question.getId(),question.getQuestion(),question.getQuestion(),question.getCategory().getId());
                listResponses.add(faq);
            });*/
            responses.add(new CategoryResponse(category.getId(), category.getName()/*,listResponses*/));});
        return responses.toArray(new CategoryResponse[0]); // 200

    }
    public CategoryResponse createCategory(String name)
    {
        CategoryModel category = new CategoryModel();
        category.setName(name);
        categoryRepository.addCategory(category);
        /*var questions = category.getQuestions();
        List<FAQResponse> listResponses = new ArrayList<>();
        questions.forEach(question -> {
            FAQResponse faq = new FAQResponse(question.getId(),question.getQuestion(),question.getQuestion(),question.getCategory().getId());
            listResponses.add(faq);
        });*/
        return new CategoryResponse(category.getId(),category.getName()/*,listResponses*/);
    }

    public CategoryResponse updateCategoryName(Long id, String newName) {
        CategoryModel updated = categoryRepository.updateCategoryName(id, newName);
        if (updated == null) return null;
        /*var questions = updated.getQuestions();
        List<FAQResponse> listResponses = new ArrayList<>();
        questions.forEach(question -> {
            FAQResponse faq = new FAQResponse(question.getId(),question.getQuestion(),question.getQuestion(),question.getCategory().getId());
            listResponses.add(faq);
        });*/
        return new CategoryResponse(updated.getId(), updated.getName()/*, listResponses*/);
    }

    public boolean deleteCategory(Long id) {
        return categoryRepository.deleteCategoryWithQuestions(id);
    }


}
