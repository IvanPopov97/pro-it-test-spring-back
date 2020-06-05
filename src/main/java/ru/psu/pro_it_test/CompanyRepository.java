package ru.psu.pro_it_test;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;


@Repository
@Transactional
public class CompanyRepository {

    private final DSLContext dsl;

    private final AggregateFunction<Integer> employeeCount = DSL.count(EMPLOYEE.ID);
    private final ru.psu.pro_it_test.tables.Company HEAD = COMPANY.as("head");

    @Autowired
    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private SelectJoinStep<?> selectAndJoin() {
        return dsl.select(COMPANY.ID, COMPANY.NAME, HEAD.ID, HEAD.NAME, employeeCount)
                .from(COMPANY)
                .leftJoin(HEAD)
                .on(HEAD.ID.eq(COMPANY.HEAD_COMPANY_ID))
                .leftJoin(EMPLOYEE)
                .on(EMPLOYEE.COMPANY_ID.eq(COMPANY.ID));
    }

    private SelectHavingStep<?> selectJoinAndGroup() {
        return selectAndJoin().groupBy(COMPANY.ID, HEAD.ID);
    }

    private SelectHavingStep<?> selectJoinFilterAndGroup(Condition filter) {
        return selectAndJoin().where(filter).groupBy(COMPANY.ID, HEAD.ID);
    }

    private Result<?> extractForPage (SelectHavingStep<?> prevStep, Pageable pageRequest) {
        return prevStep
                .limit(pageRequest.getPageSize())
                .offset(pageRequest.getOffset())
                .fetch();
    }

    private List<Company> mapToCompany(Result<?> result) {
        return result.map(record -> new Company(
                COMPANY.ID.get(record),
                COMPANY.NAME.get(record),
                HEAD.ID.get(record),
                HEAD.NAME.get(record),
                employeeCount.get(record)
        ));
    }

    private String getRegexp(String name, boolean startsWith) {
        return startsWith ? name + "%" : name;
    }

    private Condition getFilter(String name, boolean startsWith) {
        return COMPANY.NAME.likeIgnoreCase(getRegexp(name, startsWith));
    }

    public List<Company> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilter(name, startsWith);

        return mapToCompany(
                extractForPage(selectJoinFilterAndGroup(filter), pageRequest)
        );
    }

    public List<Company> findAll(Pageable pageRequest) {
        return mapToCompany(
                extractForPage(selectJoinAndGroup(), pageRequest)
        );
    }

    public int findCount(String name, boolean startsWith) {
        SelectJoinStep<?> afterSelect = dsl.selectCount().from(COMPANY);
        return name.equals("") ?
                afterSelect.fetchOne(0, int.class) :
                afterSelect.where(getFilter(name, startsWith)).fetchOne(0, int.class);
    }
}
