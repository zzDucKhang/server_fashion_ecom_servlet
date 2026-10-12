package com.fashionshop.dataaccess.dao.cartorder;

import com.fashionshop.dataaccess.base.AbstractJpaDAO;
import com.fashionshop.business.entity.OrderItem;

public class OrderItemDAO extends AbstractJpaDAO<OrderItem, Integer> {
    public OrderItemDAO() {
        super(OrderItem.class);
    }
}

