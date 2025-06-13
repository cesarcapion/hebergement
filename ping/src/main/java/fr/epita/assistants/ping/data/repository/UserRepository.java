package fr.epita.assistants.ping.data.repository;

import java.util.List;
import java.util.UUID;

import fr.epita.assistants.ping.data.model.UserModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class UserRepository {
    @Inject
    EntityManager entityManager;

    public void persist(UserModel user) { 
        entityManager.persist(user);
    }

    public UserModel findById(UUID id) {
        return entityManager.find(UserModel.class, id);
    }

    public UserModel findByLogin(String login) {
        
        TypedQuery<UserModel> query = entityManager.createQuery(
            "SELECT u FROM UserModel u WHERE u.login = :login", UserModel.class
        );
        query.setParameter("login", login);
        return query.getSingleResult();
    }

    public List<UserModel> listAll() {
        TypedQuery<UserModel> query = entityManager.createQuery("FROM UserModel", UserModel.class);
        return query.getResultList();
    }

    public void delete(UserModel user) {
        entityManager.remove(user);
    }
}