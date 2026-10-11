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

    public Optional<Product> findByName(String name) {
        return execute(em -> {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.defaultName = :name AND p.deletedAt IS NULL", Product.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        });
    }

    public List<Product> findWithFilter(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT p FROM Product p WHERE p.deletedAt IS NULL ");
            appendConditions(jpql, queryDTO);
            jpql.append(" ORDER BY p.createdAt DESC");

            TypedQuery<Product> query = em.createQuery(jpql.toString(), Product.class);
            setParameters(query, queryDTO);

            query.setFirstResult((queryDTO.getPage() - 1) * queryDTO.getLimit());
            query.setMaxResults(queryDTO.getLimit());
            return query.getResultList();
        });
    }

    public long countWithFilter(ProductQueryDTO queryDTO) {
        return execute(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL ");
            appendConditions(jpql, queryDTO);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            setParameters(query, queryDTO);
            return query.getSingleResult();
        });
    }

    private void appendConditions(StringBuilder jpql, ProductQueryDTO query) {
        if (query.getCategoryId() != null) {
            jpql.append(" AND p.category.id = :categoryId");
        }
        if (query.getBrandId() != null) {
            jpql.append(" AND p.brand.id = :brandId");
        }
        if (query.getSearch() != null && !query.getSearch().isBlank()) {
            jpql.append(" AND LOWER(p.defaultName) LIKE LOWER(:search)");
        }
    }

    private void setParameters(TypedQuery<?> query, ProductQueryDTO queryDTO) {
        if (queryDTO.getCategoryId() != null) {
            query.setParameter("categoryId", queryDTO.getCategoryId());
        }
        if (queryDTO.getBrandId() != null) {
            query.setParameter("brandId", queryDTO.getBrandId());
        }
        if (queryDTO.getSearch() != null && !queryDTO.getSearch().isBlank()) {
            query.setParameter("search", "%" + queryDTO.getSearch().trim() + "%");
        }
    }
}