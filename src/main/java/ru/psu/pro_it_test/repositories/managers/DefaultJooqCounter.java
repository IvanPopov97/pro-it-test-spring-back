package ru.psu.pro_it_test.repositories.managers;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;

public abstract class DefaultJooqCounter implements JooqCounter {
    private final Table<?> table;

    protected DefaultJooqCounter(Table<?> table) {
        this.table = table;
    }

    @Override
    public long count(DSLContext dsl) {
        return dsl.selectCount().from(table).fetchOne(0, long.class);
    }

    @Override
    public long count(DSLContext dsl, Condition condition) {
        return dsl.selectCount().from(table).where(condition).fetchOne(0, long.class);
    }
}
