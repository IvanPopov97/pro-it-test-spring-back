package ru.psu.pro_it_test.repositories.conditions;

import org.jooq.Condition;

// T - тип идентификатора: Int, Long, String или UUID
public interface ConditionFactory<T> {
    Condition filterByName(String name, boolean startsWith);
    Condition filterByParent();
    Condition filterByParent(T parentId);
}