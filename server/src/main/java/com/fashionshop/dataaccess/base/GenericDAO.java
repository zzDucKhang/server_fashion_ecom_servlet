package com.fashionshop.dataaccess.base;

import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    T save(T entity);
    T update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findPaginated(int page, int limit);
    long count();
}