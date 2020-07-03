package ru.psu.pro_it_test.repositories.managers;

import org.jooq.Condition;
import org.jooq.DSLContext;

public interface JooqCounter {
    long count(DSLContext dsl);
    long count(DSLContext dsl, Condition condition);
}
