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

    @Autowired
    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    protected SelectJoinStep<?> select() {
        SelectJoinStep<?> select = dsl.select(COMPANY.ID, COMPANY.NAME, HAS_CHILD)
                .from(COMPANY);
        System.out.println(select.getSQL());
        return select;
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

    protected Condition getFilterByName(String name, boolean startsWith) {
        return COMPANY.NAME.likeIgnoreCase(getRegexpForFilterByName(name, startsWith));
    }

    public Page<Company> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(name, startsWith);

        return getPage(selectJoinFilterAndGroup(filter), pageRequest, COMPANY.ID);
    }

    public Page<Company> findAll(Pageable pageRequest) {
        return getPage(selectJoinAndGroup(), pageRequest, COMPANY.ID);
    }

    public List<Company> findDaughters(Long parentId) {
        Condition filter = COMPANY.HEAD_COMPANY_ID.eq(parentId);
        return selectAndFilter(filter).fetchInto(Company.class);
    }

    public List<Company> findParents() {
        Condition filter = COMPANY.HEAD_COMPANY_ID.isNull();
        return selectAndFilter(filter).fetchInto(Company.class);
    }
}
