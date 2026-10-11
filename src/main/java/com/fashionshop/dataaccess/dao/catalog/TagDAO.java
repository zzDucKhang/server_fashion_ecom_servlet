package com.fashionshop.dataaccess.dao.catalog;

import com.fashionshop.business.entity.Tag;
import com.fashionshop.dataaccess.base.AbstractJpaDAO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagDAO extends AbstractJpaDAO<Tag, Integer> {
    public TagDAO() {
        super(Tag.class);
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