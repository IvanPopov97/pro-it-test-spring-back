package ru.psu.pro_it_test.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.psu.pro_it_test.dto.EmployeeDto;
import ru.psu.pro_it_test.repositories.conditions.DefaultEmployeeConditionFactory;
import ru.psu.pro_it_test.repositories.conditions.EmployeeConditionFactory;
import ru.psu.pro_it_test.repositories.filters.*;
import ru.psu.pro_it_test.repositories.managers.EmployeeManager;
import ru.psu.pro_it_test.repositories.managers.JooqManager;

import java.util.List;

@Repository
public class DefaultEmployeeRepo extends JooqRepo<EmployeeDto, Long> implements EmployeeRepo {

    private final DSLContext dsl;

    private static final JooqFilter<EmployeeDto> listFilter = new EmployeeListFilter();
    private static final JooqFilter<EmployeeDto> treeFilter = new EmployeeTreeFilter();
    private static final JooqFilter<EmployeeDto> nameFilter = new EmployeeNameListFilter();

    private static final EmployeeConditionFactory conditionFactory = new DefaultEmployeeConditionFactory();

    private static final JooqManager<EmployeeDto, Long> manager = new EmployeeManager();

    @Autowired
    protected DefaultEmployeeRepo(DSLContext dsl) {
        super(dsl, listFilter, treeFilter, nameFilter, conditionFactory, manager);
        this.dsl = dsl;
    }

    @Override
    public List<EmployeeDto> findNamesByCompanyId(Long companyId) {
        return nameFilter.apply(dsl, conditionFactory.filterByCompanyId(companyId));
    }

    @Override
    public List<EmployeeDto> findByCompanyName(String companyName, boolean startsWith, long skip, int limit) {
        return listFilter.apply(
                dsl,
                conditionFactory.filterByCompanyName(companyName, startsWith),
                skip,
                limit
        );
    }

    @Override
    public long findCountByCompanyName(String companyName, boolean startsWith) {
        return manager.count(dsl, conditionFactory.filterByName(companyName, startsWith));
    }
}
