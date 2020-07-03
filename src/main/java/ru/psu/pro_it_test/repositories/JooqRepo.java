package ru.psu.pro_it_test.repositories;

import org.jooq.DSLContext;
import ru.psu.pro_it_test.repositories.conditions.ConditionFactory;
import ru.psu.pro_it_test.repositories.filters.JooqFilter;
import ru.psu.pro_it_test.repositories.managers.JooqManager;

import java.util.List;

// T - тип Dto, U - тип первичного ключа
public abstract class JooqRepo<T, U> implements Repo<T, U> {

    private final DSLContext dsl;
    private final JooqFilter<T> listFilter;
    private final JooqFilter<T> treeFilter;
    private final JooqFilter<T> nameFilter;
    private final ConditionFactory<U> conditionFactory;
    private final JooqManager<T, U> manager;

    protected JooqRepo(DSLContext dsl,
                       JooqFilter<T> listFilter,
                       JooqFilter<T> treeFilter,
                       JooqFilter<T> nameFilter,
                       ConditionFactory<U> conditionFactory,
                       JooqManager<T, U> manager) {
        this.dsl = dsl;
        this.listFilter = listFilter;
        this.treeFilter = treeFilter;
        this.nameFilter = nameFilter;
        this.conditionFactory = conditionFactory;
        this.manager = manager;
    }

    @Override
    public List<T> findAll(long skip, int limit) {
        return listFilter.apply(dsl, skip, limit);
    }

    @Override
    public List<T> findAll(boolean onlyRequiredFields) {
        return onlyRequiredFields ? nameFilter.apply(dsl) : listFilter.apply(dsl);
    }

    @Override
    public List<T> findByName(String name, boolean startsWith, long offset, int limit) {
        return listFilter.apply(dsl, conditionFactory.filterByName(name, startsWith), offset, limit);
    }

    @Override
    public List<T> findRootItems() {
        return treeFilter.apply(dsl, conditionFactory.filterByParent());
    }

    @Override
    public List<T> findChildItems(U parentId) {
        return treeFilter.apply(dsl, conditionFactory.filterByParent(parentId));
    }

    @Override
    public long findCount() {
        return manager.count(dsl);
    }

    @Override
    public long findCount(String name, boolean startsWith) {
        return manager.count(dsl, conditionFactory.filterByName(name, startsWith));
    }

    @Override
    public T add(T item) {
        return manager.add(dsl, item);
    }

    @Override
    public boolean remove(U itemId) {
        return manager.remove(dsl, itemId);
    }

    @Override
    public boolean update(T item) {
        return manager.update(dsl, item);
    }
}
