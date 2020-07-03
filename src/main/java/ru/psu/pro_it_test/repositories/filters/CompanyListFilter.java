package ru.psu.pro_it_test.repositories.filters;

import org.jooq.Field;
import org.jooq.impl.DSL;
import ru.psu.pro_it_test.dto.CompanyDto;
import ru.psu.pro_it_test.tables.Company;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

public class CompanyListFilter extends DefaultJooqFilter<CompanyDto> {

    private static final Company HEAD = COMPANY.as("head");
    private static final Field<Integer> EMPLOYEE_COUNT = DSL.count(EMPLOYEE.ID).as("employee_count");

    public CompanyListFilter() {
        super(List.of(COMPANY.ID, COMPANY.NAME, EMPLOYEE_COUNT, HEAD.ID, HEAD.NAME),
                COMPANY.leftJoin(HEAD).on(HEAD.ID.eq(COMPANY.HEAD_COMPANY_ID))
                        .leftJoin(EMPLOYEE).on(EMPLOYEE.COMPANY_ID.eq(COMPANY.ID)),
                COMPANY.ID,
                List.of(COMPANY.ID, HEAD.ID),
                record -> {
                    Long headCompanyId = HEAD.ID.get(record);
                    CompanyDto headCompany = headCompanyId == null
                            ? null
                            : new CompanyDto(headCompanyId, HEAD.NAME.get(record));
                    return CompanyDto.builder()
                            .id(COMPANY.ID.get(record))
                            .name(COMPANY.NAME.get(record))
                            .employeeCount(EMPLOYEE_COUNT.get(record))
                            .headCompany(headCompany)
                            .build();
                });
    }
}
