package ru.psu.pro_it_test.repositories.filters;

import ru.psu.pro_it_test.dto.CompanyDto;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;

public class CompanyNameListFilter extends DefaultJooqFilter<CompanyDto> {

    public CompanyNameListFilter() {
        super(List.of(COMPANY.ID, COMPANY.NAME),
                COMPANY,
                COMPANY.NAME,
                record -> CompanyDto.builder()
                        .id(COMPANY.ID.get(record))
                        .name(COMPANY.NAME.get(record))
                        .build());
    }
}
