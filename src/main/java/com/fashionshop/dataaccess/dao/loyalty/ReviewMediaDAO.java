package com.fashionshop.dataaccess.dao.loyalty;

import com.fashionshop.business.entity.ReviewMedia;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

public class ReviewMediaDAO extends AbstractJpaDAO<ReviewMedia, Integer> {
    public ReviewMediaDAO() {
        super(ReviewMedia.class);
    }
}