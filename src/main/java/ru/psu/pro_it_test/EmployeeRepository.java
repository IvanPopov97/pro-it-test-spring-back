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
public class EmployeeRepository extends JooqRepository<Employee> {

    private final DSLContext dsl;

    private final ru.psu.pro_it_test.tables.Employee BOSS = EMPLOYEE.as("boss");

    @Autowired
    public EmployeeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    protected SelectJoinStep<?> select() {
        return dsl.select(EMPLOYEE.ID, EMPLOYEE.NAME)
                .from(EMPLOYEE);
    }

    protected SelectJoinStep<?> selectCount() {
        return dsl.selectCount().from(EMPLOYEE);
    }

    protected SelectJoinStep<?> selectAndJoin() {
        return dsl.select(EMPLOYEE.ID, EMPLOYEE.NAME, COMPANY.ID, COMPANY.NAME, BOSS.ID, BOSS.NAME)
                .from(EMPLOYEE)
                .leftJoin(COMPANY)
                .on(COMPANY.ID.eq(EMPLOYEE.COMPANY_ID))
                .leftJoin(BOSS)
                .on(BOSS.ID.eq(EMPLOYEE.BOSS_ID));
    }

    @Override
    protected List<Employee> mapToDto(Result<?> result) {
        return result.map(record -> new Employee(
                EMPLOYEE.ID.get(record),
                EMPLOYEE.NAME.get(record),
                COMPANY.ID.get(record),
                COMPANY.NAME.get(record),
                BOSS.ID.get(record),
                BOSS.NAME.get(record)
        ));
    }

    protected Condition getFilterByName(String name, boolean startsWith) {
        return COMPANY.NAME.likeIgnoreCase(getRegexpForFilterByName(name, startsWith));
    }

    public Page<Employee> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(name, startsWith);

        return getPage(selectJoinAndFilter(filter), pageRequest, COMPANY.ID);
    }

    public Page<Employee> findAll(Pageable pageRequest) {
        return getPage(selectAndJoin(), pageRequest, COMPANY.ID);
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
