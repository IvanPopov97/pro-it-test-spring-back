package ru.psu.pro_it_test.repositories.filters;

import org.jooq.Field;
import ru.psu.pro_it_test.dto.EmployeeDto;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

public class EmployeeTreeFilter extends DefaultJooqFilter<EmployeeDto> {

    private static final Field<Boolean> HAS_CHILD = field(
            EMPLOYEE.ID.eq(any(select(EMPLOYEE.BOSS_ID).from(EMPLOYEE)))
    ).as("has_child");

    public EmployeeTreeFilter() {
        super(List.of(EMPLOYEE.ID, EMPLOYEE.NAME, HAS_CHILD),
                EMPLOYEE,
                EMPLOYEE.NAME,
                record -> EmployeeDto.builder()
                        .id(EMPLOYEE.ID.get(record))
                        .name(EMPLOYEE.NAME.get(record))
                        .hasChild(HAS_CHILD.get(record))
                        .build());
    }
}
