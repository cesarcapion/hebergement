/*package fr.epita.assistants.ping.data.repository;

import fr.epita.assistants.ping.data.model.CategoryModel;
import fr.epita.assistants.ping.data.model.FAQModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<CategoryModel> {
    @Inject
    FAQRepository  faqRepository;

    public CategoryModel getById(Long id) {
        return findById(id);
    }
    @Transactional
    public void addCategory(CategoryModel categoryModel) {
        persist(categoryModel);
    }

    public List<CategoryModel> listAllCategories() {
        return listAll();
    }
    @Transactional
    public CategoryModel updateCategoryName(Long id, String newName) {
        CategoryModel category = findById(id);
        if (category == null) return null;
        category.setName(newName);
        return category;
    }
    @Transactional
    public boolean deleteCategoryWithQuestions(Long id) {
        CategoryModel category = findById(id);
        if (category == null) {
            return false;
        }

        category.getQuestions().forEach(question -> {faqRepository.deleteById(question.getId());});

        delete(category);
        return true;
    }
}
*/