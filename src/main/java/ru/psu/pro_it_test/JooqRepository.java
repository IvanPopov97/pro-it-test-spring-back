package ru.psu.pro_it_test;

import org.jooq.*;

import java.util.List;

public abstract class JooqRepository<T> {
    protected abstract SelectJoinStep<?> select();

    protected abstract SelectJoinStep<?> selectCount();

    protected abstract SelectJoinStep<?> selectAndJoin();

    protected abstract List<T> mapToDto(Result<?> result);

    protected abstract Condition getFilterByName(String name, boolean startsWith);

    public abstract Page<T> findByName(String name, boolean startsWith, Pageable pageRequest);

    public abstract Page<T> findAll(Pageable pageRequest);

    protected SelectConditionStep<?> selectAndFilter(Condition filter) {
        return select().where(filter);
    }

    protected SelectHavingStep<?> selectJoinAndFilter(Condition filter) {
        return selectAndJoin().where(filter);
    }

    protected Result<?> extractForPage(SelectOrderByStep<?> prevStep,
                                     Field<?> orderByField,
                                     long skip,
                                     int pageSize) {
        return prevStep
                .orderBy(orderByField)
                .limit(pageSize)
                .offset(skip)
                .fetch();
    }

    protected Page<T> getPage(SelectOrderByStep<?> prevStep,
                            Pageable request,
                            Field<?> orderByField) {
        Result<?> result = extractForPage(
                prevStep,
                orderByField,
                request.getOffset(),
                request.getPageSize() + 1 // запрашиваем на 1 больше, чтобы убедиться, что это not last page
        );

        boolean isFirst = request.getOffset() == 0;
        boolean isNotLast = result.size() > request.getPageSize();

        if (isNotLast)
            result.remove(result.size() - 1);

        return new Page<>(
                mapToDto(result),
                isFirst,
                !isNotLast,
                result.isEmpty()
        );
    }

    protected String getRegexpForFilterByName(String name, boolean startsWith) {
        return startsWith ? name + "%" : name;
    }

    public long findCount() {
        return selectCount().fetchOne(0, long.class);
    }

    public long findCount(String name, boolean startsWith) {
        return selectCount()
                .where(getFilterByName(name, startsWith))
                .fetchOne(0, long.class);
    }
}
