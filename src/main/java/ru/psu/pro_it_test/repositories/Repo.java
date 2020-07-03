package ru.psu.pro_it_test.repositories;

import java.util.List;

// T - target entity type, U - entity key type
public interface Repo<T, U> {
    List<T> findAll(long skip, int limit);
    List<T> findAll(boolean onlyRequiredFields);
    List<T> findByName(String name, boolean startsWith, long skip, int limit);
    List<T> findRootItems();
    List<T> findChildItems(U parentId);
    long findCount();
    long findCount(String name, boolean startsWith);
    T add(T item);
    boolean remove(U itemId);
    boolean update(T item);
}
