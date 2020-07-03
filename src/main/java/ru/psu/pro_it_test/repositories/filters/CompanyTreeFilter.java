package ru.psu.pro_it_test.repositories.filters;

import org.jooq.Field;
import ru.psu.pro_it_test.dto.CompanyDto;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static ru.psu.pro_it_test.tables.Company.COMPANY;

public class CompanyTreeFilter extends DefaultJooqFilter<CompanyDto> {

    private static final Field<Boolean> HAS_CHILD = field(
            COMPANY.ID.eq(any(select(COMPANY.HEAD_COMPANY_ID).from(COMPANY)))
    ).as("has_child");

    public CompanyTreeFilter() {
        super(List.of(COMPANY.ID, COMPANY.NAME, HAS_CHILD),
                COMPANY,
                COMPANY.NAME,
                record -> CompanyDto.builder()
                        .id(COMPANY.ID.get(record))
                        .name(COMPANY.NAME.get(record))
                        .hasChild(HAS_CHILD.get(record))
                        .build());
    }
}