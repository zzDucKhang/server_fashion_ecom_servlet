package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.ProductImage;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.List;

public class ProductImageDAO extends AbstractJpaDAO<ProductImage, Integer> {
    public ProductImageDAO() {
        super(ProductImage.class);
    }

    public List<ProductImage> findByProductId(Integer productId) {
        return execute(em -> em.createQuery(
                "SELECT img FROM ProductImage img WHERE img.product.id = :productId ORDER BY img.displayOrder ASC",
                ProductImage.class
        ).setParameter("productId", productId).getResultList());
    }
}

