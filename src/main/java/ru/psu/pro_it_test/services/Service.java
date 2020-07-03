package ru.psu.pro_it_test.services;

import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;

import java.util.List;

// T - Dto type, U - primary key type
public interface Service<T, U> {
    Page<T> findAll(Pageable request);
    Page<T> findByName(String name, boolean startsWith, Pageable request);
    List<T> findAllNames();
    List<T> findRootItems();
    List<T> findChildItems(U parentId);
    long findCount();
    long findCount(String name, boolean startsWith);
    T add(T item);
    boolean remove(U itemId);
    boolean update(T item);

    // последний элемент используется только для проверки на lastPage
    default Page<T> createPageWithoutLastItem(List<T> items, Pageable request) {
        boolean isEmpty = items.size() == 0;
        boolean isLast = isEmpty || items.size() <= request.getPageSize();
        if (!isLast) {
            items.remove(items.size() - 1);
        }
        return new Page<>(items, request.getOffset() == 0, isLast, isEmpty);
    }
}
