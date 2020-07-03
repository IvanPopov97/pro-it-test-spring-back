package ru.psu.pro_it_test.repositories.conditions;

import org.jooq.Condition;

import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;
import static ru.psu.pro_it_test.tables.Company.COMPANY;

public class DefaultEmployeeConditionFactory extends AbstractConditionFactory implements ConditionFactory<Long>, EmployeeConditionFactory {
    @Override
    public Condition filterByName(String name, boolean startsWith) {
        return super.filterByName(EMPLOYEE.NAME, name, startsWith);
    }

    @Override
    public Condition filterByParent() {
        return super.filterByParent(EMPLOYEE.BOSS_ID);
    }

    @Override
    public Condition filterByParent(Long parentId) {
        return super.filterByParent(EMPLOYEE.BOSS_ID, parentId);
    }

    @Override
    public Condition filterByCompanyId(Long companyId) {
        return EMPLOYEE.COMPANY_ID.eq(companyId);
    }

    @Override
    public Condition filterByCompanyName(String companyName, boolean startsWith) {
        return super.filterByName(COMPANY.NAME, companyName, startsWith);
    }
}