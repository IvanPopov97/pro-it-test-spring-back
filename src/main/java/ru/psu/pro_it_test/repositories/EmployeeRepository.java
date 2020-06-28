package ru.psu.pro_it_test.repositories;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.psu.pro_it_test.entities.Employee;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;

import java.util.List;

import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;
import static ru.psu.pro_it_test.tables.Company.COMPANY;

@Repository
@Transactional
public class EmployeeRepository extends JooqRepository<Employee> {

    private final DSLContext dsl;

    private final ru.psu.pro_it_test.tables.Employee BOSS = EMPLOYEE.as("boss");
    private final Field<Boolean> HAS_CHILD = DSL.field(
            EMPLOYEE.ID.eq(
                    DSL.any(
                            DSL.select(EMPLOYEE.BOSS_ID).from(EMPLOYEE)
                    )
            )
    ).as("has_child");

    private final Field<?> sortField = EMPLOYEE.ID;

    @Autowired
    public EmployeeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    protected SelectJoinStep<?> select() {
        return dsl.select(EMPLOYEE.ID, EMPLOYEE.NAME, HAS_CHILD)
                .from(EMPLOYEE);
    }

    @Override
    protected SelectJoinStep<?> select(List<Field<?>> fields) {
        return dsl.select(fields).from(EMPLOYEE);
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

    private Field<?> getNameField(boolean isEmployeeName) {
        return isEmployeeName ? EMPLOYEE.NAME : COMPANY.NAME;
    }

    public Page<Employee> findByName(String name, boolean isEmployeeName, boolean startsWith, Pageable pageRequest) {

        Field<?> nameField = getNameField(isEmployeeName);
        Condition filter = getFilterByName(nameField, name, startsWith);

        return getPage(selectJoinAndFilter(filter), pageRequest, sortField);
    }

    public Page<Employee> findByName(String name, String companyName, boolean startsWith, Pageable pageRequest) {

        Condition filterByName = getFilterByName(EMPLOYEE.NAME, name, startsWith);
        Condition filterByCompanyName = getFilterByName(COMPANY.NAME, companyName, startsWith);

        return getPage(
                selectJoinAndFilter(
                        filterByName.and(filterByCompanyName)
                ),
                pageRequest,
                sortField
        );
    }


    public Page<Employee> findAll(Pageable pageRequest) {
        return getPage(selectAndJoin(), pageRequest, sortField);
    }

    public long findCount(String name, boolean isEmployeeName, boolean startsWith) {
        Field<?> nameField = getNameField(isEmployeeName);
        Condition filter = getFilterByName(nameField, name, startsWith);
        return findCount(filter);
    }

    public long findCount(String name, String companyName, boolean startsWith) {
        Condition filterByName = getFilterByName(EMPLOYEE.NAME, name, startsWith);
        Condition filterByCompanyName = getFilterByName(COMPANY.NAME, companyName, startsWith);
        return findCount(filterByName.and(filterByCompanyName));
    }

    public List<Employee> findSubordinates(Long bossId) {
        Condition filter = EMPLOYEE.BOSS_ID.eq(bossId);
        return selectAndFilter(filter).fetchInto(Employee.class);
    }

    public List<Employee> findRootEmployees() {
        Condition filter = EMPLOYEE.BOSS_ID.isNull();
        return selectAndFilter(filter).fetchInto(Employee.class);
    }

    public List<Employee> findNamesByCompanyId(long id) {
        Condition filter = EMPLOYEE.COMPANY_ID.eq(id);
        return select(List.of(EMPLOYEE.ID, EMPLOYEE.NAME))
                .where(filter)
                .fetchInto(Employee.class);
    }

    public long add(Employee employee) {
        Long companyId = employee.getCompany() == null ? null : employee.getCompany().getId();
        Long bossId = employee.getBoss() == null ? null : employee.getBoss().getId();
        return dsl.insertInto(EMPLOYEE, EMPLOYEE.NAME, EMPLOYEE.COMPANY_ID, EMPLOYEE.BOSS_ID)
                .values(employee.getName(), companyId, bossId)
                .returning()
                .fetchOne()
                .getId();
    }
}
