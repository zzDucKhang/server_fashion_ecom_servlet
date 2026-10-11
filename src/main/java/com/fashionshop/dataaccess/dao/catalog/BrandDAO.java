package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Brand;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;
import java.util.Optional;

public class BrandDAO extends AbstractJpaDAO<Brand, Integer> {

    public BrandDAO() {
        super(Brand.class);
    }

    public Optional<Brand> findByName(String name) {
        return execute(em -> {
            List<Brand> list = em.createQuery(
                            "SELECT b FROM Brand b WHERE LOWER(b.defaultName) = LOWER(:name) AND b.deletedAt IS NULL", Brand.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Brand> findAllActive() {
        return execute(em -> em.createQuery(
                        "SELECT b FROM Brand b WHERE b.deletedAt IS NULL ORDER BY b.createdAt DESC", Brand.class)
                .getResultList());
    }

    public List<Brand> findPaginatedActive(int page, int limit, String search) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT b FROM Brand b WHERE b.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(b.defaultName) LIKE LOWER(:search)");
            }
            jpql.append(" ORDER BY b.createdAt DESC");

            var query = em.createQuery(jpql.toString(), Brand.class);
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
            StringBuilder jpql = new StringBuilder("SELECT COUNT(b) FROM Brand b WHERE b.deletedAt IS NULL");
            if (search != null && !search.isBlank()) {
                jpql.append(" AND LOWER(b.defaultName) LIKE LOWER(:search)");
            }

            var query = em.createQuery(jpql.toString(), Long.class);
            if (search != null && !search.isBlank()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }
            return query.getSingleResult();
        });
    }
}