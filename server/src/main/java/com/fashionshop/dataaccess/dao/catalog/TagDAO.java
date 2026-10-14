package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TagDAO extends AbstractJpaDAO<Tag, Integer> {
    public TagDAO() {
        super(Tag.class);
    }

    public Optional<Tag> findByName(String name) {
        return execute(em -> {
            List<Tag> list = em.createQuery(
                            "SELECT t FROM Tag t WHERE LOWER(t.name) = LOWER(:name) AND t.deletedAt IS NULL", Tag.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Tag> findPaginatedActive(int page, int limit, String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT t FROM Tag t WHERE t.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(t.name) LIKE LOWER(:search)");
            }
            jpql.append(" ORDER BY t.createdAt DESC");

            var query = em.createQuery(jpql.toString(), Tag.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public long countActive(String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM Tag t WHERE t.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(t.name) LIKE LOWER(:search)");
            }

            var query = em.createQuery(jpql.toString(), Long.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }
            return query.getSingleResult();
        });
    }

    public Set<Tag> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        return execute(em -> {
            List<Tag> tags = em.createQuery("SELECT t FROM Tag t WHERE t.id IN :ids AND t.deletedAt IS NULL", Tag.class)
                    .setParameter("ids", ids)
                    .getResultList();
            return new HashSet<>(tags);
        });
    }
}