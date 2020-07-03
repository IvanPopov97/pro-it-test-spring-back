package ru.psu.pro_it_test.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.psu.pro_it_test.dto.CompanyDto;
import ru.psu.pro_it_test.repositories.conditions.CompanyConditionFactory;
import ru.psu.pro_it_test.repositories.conditions.ConditionFactory;
import ru.psu.pro_it_test.repositories.filters.CompanyListFilter;
import ru.psu.pro_it_test.repositories.filters.CompanyNameListFilter;
import ru.psu.pro_it_test.repositories.filters.CompanyTreeFilter;
import ru.psu.pro_it_test.repositories.filters.JooqFilter;
import ru.psu.pro_it_test.repositories.managers.CompanyManager;
import ru.psu.pro_it_test.repositories.managers.JooqManager;

@Repository
public class CompanyRepo extends JooqRepo<CompanyDto, Long> {

    private static final JooqFilter<CompanyDto> listFilter = new CompanyListFilter();
    private static final JooqFilter<CompanyDto> treeFilter = new CompanyTreeFilter();
    private static final JooqFilter<CompanyDto> nameFilter = new CompanyNameListFilter();

    private static final ConditionFactory<Long> conditionFactory = new CompanyConditionFactory();

    private static final JooqManager<CompanyDto, Long> manager = new CompanyManager();

    @Autowired
    protected CompanyRepo(DSLContext dsl) {
        super(dsl, listFilter, treeFilter, nameFilter, conditionFactory, manager);
    }
}