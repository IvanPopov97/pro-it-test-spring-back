package ru.psu.pro_it_test.repositories.filters;

import ru.psu.pro_it_test.dto.CompanyDto;
import ru.psu.pro_it_test.dto.EmployeeDto;
import ru.psu.pro_it_test.tables.Employee;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

public class EmployeeListFilter extends DefaultJooqFilter<EmployeeDto> {
    private static final Employee BOSS = EMPLOYEE.as("boss");

    public EmployeeListFilter() {
        super(List.of(EMPLOYEE.ID, EMPLOYEE.NAME, COMPANY.ID, COMPANY.NAME, BOSS.ID, BOSS.NAME),
                EMPLOYEE.leftJoin(COMPANY).on(COMPANY.ID.eq(EMPLOYEE.COMPANY_ID))
                        .leftJoin(BOSS).on(BOSS.ID.eq(EMPLOYEE.BOSS_ID)),
                EMPLOYEE.ID,
                record -> {
                    Long companyId = COMPANY.ID.get(record);
                    Long bossId = BOSS.ID.get(record);
                    CompanyDto company = companyId == null
                            ? null
                            : new CompanyDto(companyId, COMPANY.NAME.get(record));
                    EmployeeDto boss = bossId == null ? null : new EmployeeDto(bossId, BOSS.NAME.get(record));
                    return EmployeeDto.builder()
                            .id(EMPLOYEE.ID.get(record))
                            .name(EMPLOYEE.NAME.get(record))
                            .company(company)
                            .boss(boss)
                            .build();
                });
    }
}
