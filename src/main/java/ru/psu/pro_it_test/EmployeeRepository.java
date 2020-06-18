package ru.psu.pro_it_test;

import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;
import static ru.psu.pro_it_test.tables.Company.COMPANY;

@Repository
@Transactional
public class EmployeeRepository {

    private final DSLContext dsl;

    private final ru.psu.pro_it_test.tables.Employee BOSS = EMPLOYEE.as("boss");

    @Autowired
    public EmployeeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private SelectJoinStep<?> select() {
        return dsl.select(EMPLOYEE.ID, EMPLOYEE.NAME)
                .from(EMPLOYEE);
    }

    private SelectConditionStep<?> selectAndFilter(Condition filter) {
        return select().where(filter);
    }

    private SelectJoinStep<?> selectAndJoin() {
        return dsl.select(EMPLOYEE.ID, EMPLOYEE.NAME, COMPANY.ID, COMPANY.NAME, BOSS.ID, BOSS.NAME)
                .from(EMPLOYEE)
                .leftJoin(COMPANY)
                .on(COMPANY.ID.eq(EMPLOYEE.COMPANY_ID))
                .leftJoin(BOSS)
                .on(BOSS.ID.eq(EMPLOYEE.BOSS_ID));
    }

    private SelectHavingStep<?> selectJoinAndFilter(Condition filter) {
        return selectAndJoin().where(filter);
    }

    private Result<?> extractForPage (SelectOrderByStep<?> prevStep, long skip, int pageSize) {
        return prevStep
                .orderBy(EMPLOYEE.ID)
                .limit(pageSize)
                .offset(skip)
                .fetch();
    }

    // код getPage и extractForPage дублируется
    private Page<Employee> getPage (SelectOrderByStep<?> prevStep, Pageable request) {
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
                mapToEmployee(result),
                isFirst,
                !isNotLast,
                result.isEmpty()
        );
    }
    
    private List<Employee> mapToEmployee(Result<?> result) {
        return result.map(record -> new Employee(
                EMPLOYEE.ID.get(record),
                EMPLOYEE.NAME.get(record),
                COMPANY.ID.get(record),
                COMPANY.NAME.get(record),
                BOSS.ID.get(record),
                BOSS.NAME.get(record)
        ));
    }

    private Condition getFilterByName(String name, boolean startsWith) {
        String regexp = startsWith ? name + "%" : name;
        return COMPANY.NAME.likeIgnoreCase(regexp);
    }

    public Page<Employee> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(name, startsWith);

        return getPage(selectJoinAndFilter(filter), pageRequest);
    }

    public Page<Employee> findAll(Pageable pageRequest) {
        return getPage(selectAndJoin(), pageRequest);
    }

    private SelectJoinStep<?> selectCount() {
        return dsl.selectCount().from(EMPLOYEE);
    }

    public long findCount() {
        return selectCount().fetchOne(0, long.class);
    }

    public long findCount(String name, boolean startsWith) {
        return selectCount()
                .where(getFilterByName(name, startsWith))
                .fetchOne(0, long.class);
    }

    public List<Employee> findSubordinates(Long parentId) {
        Condition filter = EMPLOYEE.COMPANY_ID.eq(parentId);
        return selectAndFilter(filter).fetchInto(Employee.class);
    }

    public List<Employee> findHigherBosses() {
        Condition filter = EMPLOYEE.BOSS_ID.isNull();
        return selectAndFilter(filter).fetchInto(Employee.class);
    }
}
