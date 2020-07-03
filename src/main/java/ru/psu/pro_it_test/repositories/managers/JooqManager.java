package ru.psu.pro_it_test.repositories.managers;

import org.jooq.DSLContext;

public interface JooqManager<T, U> extends JooqCounter {
    T add(DSLContext dsl, T item);
    boolean remove(DSLContext dsl, U itemId);
    boolean update(DSLContext dsl, T item);
}