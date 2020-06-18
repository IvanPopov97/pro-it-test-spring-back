package ru.psu.pro_it_test;

import org.jooq.*;

import java.util.List;

public abstract class JooqRepository<T> {
    protected abstract SelectJoinStep<?> select();

    protected abstract SelectJoinStep<?> selectCount();

    protected abstract SelectConditionStep<?> selectAndFilter(Condition filter);

    protected abstract SelectJoinStep<?> selectAndJoin();

    protected abstract SelectHavingStep<?> selectJoinAndFilter(Condition filter);

    private Result<?> extractForPage(SelectOrderByStep<?> prevStep,
                                     Field<?> orderByField,
                                     long skip,
                                     int pageSize) {
        return prevStep
                .orderBy(orderByField)
                .limit(pageSize)
                .offset(skip)
                .fetch();
    }

    protected abstract List<T> mapToDto(Result<?> result);

    private Page<T> getPage(SelectOrderByStep<?> prevStep,
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

    private String getRegexpForFilterByName(String name, boolean startsWith) {
        return startsWith ? name + "%" : name;
    }

    protected abstract Condition getFilterByName(String name, boolean startsWith);

    public abstract Page<T> findByName(String name, boolean startsWith, Pageable pageRequest);

    public abstract Page<Employee> findAll(Pageable pageRequest);

    public long findCount() {
        return selectCount().fetchOne(0, long.class);
    }

    public long findCount(String name, boolean startsWith) {
        return selectCount()
                .where(getFilterByName(name, startsWith))
                .fetchOne(0, long.class);
    }

}
