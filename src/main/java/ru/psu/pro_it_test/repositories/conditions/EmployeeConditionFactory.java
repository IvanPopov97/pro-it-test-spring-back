package ru.psu.pro_it_test.repositories.conditions;

import org.jooq.Condition;

public interface EmployeeConditionFactory extends ConditionFactory<Long> {
    Condition filterByCompanyId(Long companyId);
    Condition filterByCompanyName(String companyName, boolean startsWith);
}
