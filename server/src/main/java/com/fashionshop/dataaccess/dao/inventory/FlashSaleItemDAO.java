package com.fashionshop.dataaccess.dao.inventory;

import com.fashionshop.business.entity.FlashSaleItem;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

public class FlashSaleItemDAO extends AbstractJpaDAO<FlashSaleItem, Integer> {
    public FlashSaleItemDAO() {
        super(FlashSaleItem.class);
    }
}