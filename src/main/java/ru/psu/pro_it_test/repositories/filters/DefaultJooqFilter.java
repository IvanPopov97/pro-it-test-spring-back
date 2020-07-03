package ru.psu.pro_it_test.repositories.filters;

import org.jooq.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DefaultJooqFilter<T> implements JooqFilter<T> {

    private final List<Field<?>> fields;
    private final TableLike<?> table;
    private final Field<?> sortField;
    private List<Field<?>> groupByFields = null;
    private final Function<? super Record,T> mapper;

    public DefaultJooqFilter(List<Field<?>> fields,
                             TableLike<?> table,
                             Field<?> sortField,
                             List<Field<?>> groupByFields,
                             Function<Record, T> mapper) {
        this.fields = fields;
        this.table = table;
        this.sortField = sortField;
        this.groupByFields = groupByFields;
        this.mapper = mapper;
    }

    public DefaultJooqFilter(List<Field<?>> fields,
                             TableLike<?> table,
                             Field<?> sortField,
                             Function<Record, T> mapper) {
        this.fields = fields;
        this.table = table;
        this.sortField = sortField;
        this.mapper = mapper;
    }

    private SelectHavingStep<?> select(DSLContext dsl) {
        return groupByFields == null
                ? dsl.select(fields).from(table)
                : dsl.select(fields).from(table).groupBy(groupByFields);
    }

    private SelectHavingStep<?> select(DSLContext dsl, Condition condition) {
        return groupByFields == null
                ? dsl.select(fields).from(table).where(condition)
                : dsl.select(fields).from(table).where(condition).groupBy(groupByFields);
    }

    @Override
    public List<T> apply(DSLContext dsl) {
        return mapToList(select(dsl).orderBy(sortField).fetch());
    }

    @Override
    public List<T> apply(DSLContext dsl, Condition condition) {
        return mapToList(select(dsl, condition).orderBy(sortField).fetch());
    }

    @Override
    public List<T> apply(DSLContext dsl, long skip, int limit) {
        return mapToList(select(dsl)
                .orderBy(sortField)
                .offset(skip)
                .limit(limit)
                .fetch());
    }

    @Override
    public List<T> apply(DSLContext dsl, Condition condition, long skip, int limit) {
        return mapToList(select(dsl, condition)
                .orderBy(sortField)
                .offset(skip)
                .limit(limit)
                .fetch());
    }

    private List<T> mapToList(Result<?> result) {
        return result.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
