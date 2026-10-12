package com.fashionshop.dataaccess.dao.fulfillment;

import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.business.entity.ReturnMedia;

public class ReturnMediaDAO extends AbstractJpaDAO<ReturnMedia, Integer> {
    public ReturnMediaDAO() {
        super(ReturnMedia.class);
    }
}

