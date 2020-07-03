package ru.psu.pro_it_test.repositories.filters;

import ru.psu.pro_it_test.dto.EmployeeDto;

import java.util.List;

import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

public class EmployeeNameListFilter extends DefaultJooqFilter<EmployeeDto> {
    public EmployeeNameListFilter() {
        super(List.of(EMPLOYEE.ID, EMPLOYEE.NAME), EMPLOYEE, EMPLOYEE.NAME,
                record -> EmployeeDto.builder()
                        .id(EMPLOYEE.ID.get(record))
                        .name(EMPLOYEE.NAME.get(record))
                        .build());
    }
}
