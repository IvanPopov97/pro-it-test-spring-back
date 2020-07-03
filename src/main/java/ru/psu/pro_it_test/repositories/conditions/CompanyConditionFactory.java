package ru.psu.pro_it_test.repositories.conditions;

import org.jooq.Condition;

import static ru.psu.pro_it_test.tables.Company.COMPANY;

public class CompanyConditionFactory extends AbstractConditionFactory implements ConditionFactory<Long> {

    public Condition filterByName(String name, boolean startsWith) {
        return super.filterByName(COMPANY.NAME, name, startsWith);
    }

    @Override
    public Condition filterByParent() {
        return super.filterByParent(COMPANY.HEAD_COMPANY_ID);
    }

    @Override
    public Condition filterByParent(Long parentId) {
        return super.filterByParent(COMPANY.HEAD_COMPANY_ID, parentId);
    }
}
