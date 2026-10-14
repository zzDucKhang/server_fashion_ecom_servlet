package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Category;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class CategoryDAO extends AbstractJpaDAO<Category, Integer> {

    public CategoryDAO() {
        super(Category.class);
    }

    @Override
    public Optional<Category> findById(Integer id) {
        if (id == null) return Optional.empty();
        return execute(em -> {
            List<Category> list = em.createQuery(
                            "SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.id = :id AND c.deletedAt IS NULL",
                            Category.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public Optional<Category> findByName(String name) {
        return execute(em -> {
            List<Category> list = em.createQuery(
                            "SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE LOWER(c.defaultName) = LOWER(:name) AND c.deletedAt IS NULL",
                            Category.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Category> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.deletedAt IS NULL ORDER BY c.createdAt DESC",
                        Category.class)
                .getResultList());
    }

    public List<Category> findByParentId(Integer parentId) {
        return execute(em -> {
            String jpql = parentId == null
                    ? "SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.parent IS NULL AND c.deletedAt IS NULL ORDER BY c.createdAt DESC"
                    : "SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.parent.id = :parentId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC";

            var query = em.createQuery(jpql, Category.class);
            if (parentId != null) {
                query.setParameter("parentId", parentId);
            }
            return query.getResultList();
        });
    }

    public List<Category> findRootCategories() {
        return findByParentId(null);
    }
}