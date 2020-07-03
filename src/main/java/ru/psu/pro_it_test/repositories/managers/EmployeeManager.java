package ru.psu.pro_it_test.repositories.managers;

import org.jooq.Condition;
import org.jooq.DSLContext;
import ru.psu.pro_it_test.dto.EmployeeDto;

import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

public class EmployeeManager extends DefaultJooqCounter implements JooqManager<EmployeeDto, Long> {

    public EmployeeManager() {
        super(EMPLOYEE);
    }

    private Long extractCompanyId(EmployeeDto employee) {
        return employee.getCompany() == null ? null : employee.getCompany().getId();
    }

    private Long extractBossId(EmployeeDto employee) {
        return employee.getBoss() == null ? null : employee.getBoss().getId();
    }

    @Override
    public EmployeeDto add(DSLContext dsl, EmployeeDto employee) {
        Long companyId = extractCompanyId(employee);
        Long bossId = extractBossId(employee);
        Long employeeId = dsl.insertInto(EMPLOYEE, EMPLOYEE.NAME, EMPLOYEE.COMPANY_ID, EMPLOYEE.BOSS_ID)
                .values(employee.getName(), companyId, bossId)
                .returning()
                .fetchOne()
                .getId();
        employee.setId(employeeId);
        return employee;
    }

    @Override
    public boolean remove(DSLContext dsl, Long employeeId) {
        Condition filterById = EMPLOYEE.ID.eq(employeeId);
        return dsl.delete(EMPLOYEE).where(filterById).execute() > 0;
    }

    @Override
    public boolean update(DSLContext dsl, EmployeeDto employee) {
        Condition filterById = EMPLOYEE.ID.eq(employee.getId());
        Long companyId = extractCompanyId(employee);
        Long bossId = extractBossId(employee);
        return dsl.update(EMPLOYEE)
                .set(EMPLOYEE.NAME, employee.getName())
                .set(EMPLOYEE.COMPANY_ID, companyId)
                .set(EMPLOYEE.BOSS_ID, bossId)
                .where(filterById)
                .execute() > 0;
    }
}
