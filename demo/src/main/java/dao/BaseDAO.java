package dao;

import java.util.List;

public interface BaseDAO<K, T> {

    T insert(T entity);

    T update(T entity);

    boolean delete(K id);

    T findById(K id);

    List<T> findAll();
} 