package ru.psu.pro_it_test.repositories;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.psu.pro_it_test.entities.Company;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

@Repository
@Transactional
public class CompanyRepository extends JooqRepository<Company> {

    private final DSLContext dsl;

    private final AggregateFunction<Integer> EMPLOYEE_COUNT = DSL.count(EMPLOYEE.ID);
    private final Field<Boolean> HAS_CHILD = DSL.field(
            COMPANY.ID.eq(
                    DSL.any(
                            DSL.select(COMPANY.HEAD_COMPANY_ID).from(COMPANY)
                    )
            )
    );

    private final ru.psu.pro_it_test.tables.Company HEAD = COMPANY.as("head");

    private final Field<?> sortField = COMPANY.ID;

    @Autowired
    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    protected SelectJoinStep<?> select() {
        return dsl.select(COMPANY.ID, COMPANY.NAME, HAS_CHILD)
                .from(COMPANY);
    }

    protected SelectJoinStep<?> selectCount() {
        return dsl.selectCount().from(COMPANY);
    }

    protected SelectJoinStep<?> selectAndJoin() {
        return dsl.select(COMPANY.ID, COMPANY.NAME, HEAD.ID, HEAD.NAME, EMPLOYEE_COUNT)
                .from(COMPANY)
                .leftJoin(HEAD)
                .on(HEAD.ID.eq(COMPANY.HEAD_COMPANY_ID))
                .leftJoin(EMPLOYEE)
                .on(EMPLOYEE.COMPANY_ID.eq(COMPANY.ID));
    }

    @Override
    protected List<Company> mapToDto(Result<?> result) {
        return result.map(record -> new Company(
                COMPANY.ID.get(record),
                COMPANY.NAME.get(record),
                HEAD.ID.get(record),
                HEAD.NAME.get(record),
                EMPLOYEE_COUNT.get(record)
        ));
    }

    private SelectHavingStep<?> selectJoinAndGroup() {
        return selectAndJoin().groupBy(COMPANY.ID, HEAD.ID);
    }

    private SelectHavingStep<?> selectJoinFilterAndGroup(Condition filter) {
        return selectAndJoin().where(filter).groupBy(COMPANY.ID, HEAD.ID);
    }

    public Page<Company> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(COMPANY.NAME, name, startsWith);

        return getPage(selectJoinFilterAndGroup(filter), pageRequest, sortField);
    }

    public Page<Company> findAll(Pageable pageRequest) {
        return getPage(selectJoinAndGroup(), pageRequest, sortField);
    }

    public long findCount(String name, boolean startsWith) {
        Condition filter = getFilterByName(COMPANY.NAME, name, startsWith);
        return findCount(filter);
    }

    public List<Company> findDaughters(Long parentId) {
        Condition filter = COMPANY.HEAD_COMPANY_ID.eq(parentId);
        return selectAndFilter(filter).fetchInto(Company.class);
    }

    public List<Company> findRootCompanies() {
        Condition filter = COMPANY.HEAD_COMPANY_ID.isNull();
        return selectAndFilter(filter).fetchInto(Company.class);
    }
}
