package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Product;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.presentation.dto.catalog.ProductQueryDTO;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ProductDAO extends AbstractJpaDAO<Product, Integer> {

    public ProductDAO() {
        super(Product.class);
    }

    public Optional<Product> findDetailById(Integer id) {
        return execute(em -> {
            List<Product> list = em.createQuery(
                            "SELECT DISTINCT p FROM Product p " +
                                    "LEFT JOIN FETCH p.category " +
                                    "LEFT JOIN FETCH p.brand " +
                                    "LEFT JOIN FETCH p.tags " +
                                    "WHERE p.id = :id AND p.deletedAt IS NULL", Product.class)
                    .setParameter("id", id)
                    .getResultList();

            if (list.isEmpty()) return Optional.empty();
            Product p = list.get(0);

            // Fetch nạp thêm images và variants để tránh N+1 query
            em.createQuery("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id ORDER BY p.id", Product.class)
                    .setParameter("id", id).getResultList();
            em.createQuery("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.id = :id ORDER BY p.id", Product.class)
                    .setParameter("id", id).getResultList();

            return Optional.of(p);
        });
    }

    public Optional<Product> findByName(String name) {
        return execute(em -> {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.defaultName) = LOWER(:name) AND p.deletedAt IS NULL", Product.class)
                    .setParameter("name", name.trim())
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Product> listProducts(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT DISTINCT p FROM Product p WHERE p.deletedAt IS NULL");
            appendFilters(jpql, queryDTO);
            jpql.append(" ORDER BY p.createdAt DESC");

            TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
            bindParams(query, queryDTO);

            query.setFirstResult((queryDTO.getPage() - 1) * queryDTO.getLimit());
            query.setMaxResults(queryDTO.getLimit());
            return query.getResultList();
        });
    }

    public long countProducts(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(DISTINCT p) FROM Product p WHERE p.deletedAt IS NULL");
            appendFilters(jpql, queryDTO);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            bindParams(query, queryDTO);
            return query.getSingleResult();
        });
    }

    private void appendFilters(StringBuilder jpql, ProductQueryDTO q) {
        if (q.getStatus() != null) {
            jpql.append(" AND p.status = :status");
        }
        if (q.getCategoryId() != null) {
            jpql.append(" AND p.category.id = :categoryId");
        }
        if (q.getBrandId() != null) {
            jpql.append(" AND p.brand.id = :brandId");
        }
        if (q.getSearch() != null && !q.getSearch().isBlank()) {
            jpql.append(" AND LOWER(p.defaultName) LIKE LOWER(:search)");
        }
    }

    private void bindParams(TypedQuery<?> query, ProductQueryDTO q) {
        if (q.getStatus() != null) {
            query.setParameter("status", q.getStatus());
        }
        if (q.getCategoryId() != null) {
            query.setParameter("categoryId", q.getCategoryId());
        }
        if (q.getBrandId() != null) {
            query.setParameter("brandId", q.getBrandId());
        }
        if (q.getSearch() != null && !q.getSearch().isBlank()) {
            query.setParameter("search", "%" + q.getSearch().trim() + "%");
        }
    }
}