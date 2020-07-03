package ru.psu.pro_it_test.repositories.filters;

import org.jooq.Condition;
import org.jooq.DSLContext;

import java.util.List;

public interface JooqFilter<T> {
    List<T> apply(DSLContext dsl);

    List<T> apply(DSLContext dsl, Condition condition);

    List<T> apply(DSLContext dsl, long skip, int limit);

    List<T> apply(DSLContext dsl, Condition condition, long skip, int limit);
}