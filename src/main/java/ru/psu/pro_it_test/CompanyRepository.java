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

    private SelectJoinStep<?> select() {
        return dsl.select(COMPANY.ID, COMPANY.NAME)
                .from(COMPANY);
    }

    private SelectConditionStep<?> selectAndFilter(Condition filter) {
        return select().where(filter);
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

    private Page<Company> getPage (SelectLimitStep<?> prevStep, Pageable request) {
        Result<?> result = extractForPage(
                prevStep,
                request.getOffset(),
                request.getPageSize() + 1 // запрашиваем на 1 больше, чтобы убедиться, что это not last page
        );

        boolean isFirst = request.getOffset() == 0;
        boolean isNotLast = result.size() > request.getPageSize();

        if (isNotLast)
            result.remove(result.size() - 1);

        return new Page<>(
                mapToCompany(result),
                isFirst,
                !isNotLast,
                result.isEmpty()
        );
    }

    private Result<?> extractForPage (SelectLimitStep<?> prevStep, long skip, int pageSize) {
        return prevStep
                .limit(pageSize)
                .offset(skip)
                .fetch();
    }

    // TODO: вынести это в класс CompanyMapper
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


    private Condition getFilterByName(String name, boolean startsWith) {
        return COMPANY.NAME.likeIgnoreCase(getRegexp(name, startsWith));
    }

    public Page<Company> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(name, startsWith);

        return getPage(selectJoinFilterAndGroup(filter), pageRequest);
    }

//    public List<Company> findAll() {
//        return mapToCompany(select().fetch());
//    }

    public Page<Company> findAll(Pageable pageRequest) {
        return getPage(selectJoinAndGroup(), pageRequest);
    }

    private SelectJoinStep<?> selectCount() {
        return dsl.selectCount().from(COMPANY);
    }

    public long findCount() {
        return selectCount().fetchOne(0, long.class);
    }

    public long findCount(String name, boolean startsWith) {
        return selectCount()
                .where(getFilterByName(name, startsWith))
                .fetchOne(0, long.class);
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
