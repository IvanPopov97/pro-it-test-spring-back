package ru.psu.pro_it_test.repositories;

import org.jooq.*;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;

import java.util.List;

public abstract class JooqRepository<T> {

    protected abstract SelectJoinStep<?> select();

    protected abstract SelectJoinStep<?> select(List<Field<?>> fields);

    protected abstract SelectJoinStep<?> selectCount();

    protected abstract SelectJoinStep<?> selectAndJoin();

    protected abstract List<T> mapToDto(Result<?> result);

    public abstract Page<T> findAll(Pageable pageRequest);

    protected SelectConditionStep<?> selectAndFilter(Condition filter) {
        return select().where(filter);
    }

    protected SelectHavingStep<?> selectJoinAndFilter(Condition filter) {
        return selectAndJoin().where(filter);
    }

    protected Result<?> extractForPage(SelectOrderByStep<?> prevStep,
                                     Field<?> sortField,
                                     long skip,
                                     int pageSize) {
        return prevStep
                .orderBy(sortField)
                .limit(pageSize)
                .offset(skip)
                .fetch();
    }

    protected Page<T> getPage(SelectOrderByStep<?> prevStep,
                            Pageable request,
                            Field<?> sortField) {
        Result<?> result = extractForPage(
                prevStep,
                sortField,
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

    protected Condition getFilterByName(Field<?> field, String name, boolean startsWith) {
        String regexp = startsWith ? name + "%" : name;
        return field.likeIgnoreCase(regexp);
    }

    protected long findCount(Condition filter) {
        return selectCount()
                .where(filter)
                .fetchOne(0, long.class);
    }

    public long findCount() {
        return selectCount().fetchOne(0, long.class);
    }
}
