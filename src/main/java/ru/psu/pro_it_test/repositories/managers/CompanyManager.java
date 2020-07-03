package ru.psu.pro_it_test.repositories.managers;

import org.jooq.Condition;
import org.jooq.DSLContext;
import ru.psu.pro_it_test.dto.CompanyDto;

import static ru.psu.pro_it_test.tables.Company.COMPANY;

public class CompanyManager extends DefaultJooqCounter implements JooqManager<CompanyDto, Long> {

    public CompanyManager() {
        super(COMPANY);
    }

    private Long extractHeadCompanyId(CompanyDto company) {
        return company.getHeadCompany() == null ? null : company.getHeadCompany().getId();
    }

    @Override
    public CompanyDto add(DSLContext dsl, CompanyDto company) {
        Long companyId = dsl.insertInto(COMPANY, COMPANY.NAME, COMPANY.HEAD_COMPANY_ID)
                .values(company.getName(), extractHeadCompanyId(company))
                .returning()
                .fetchOne()
                .getId();
        company.setId(companyId);
        return company;
    }

    @Override
    public boolean remove(DSLContext dsl, Long companyId) {
        Condition filterById = COMPANY.ID.eq(companyId);
        return dsl.delete(COMPANY).where(filterById).execute() > 0;
    }

    @Override
    public boolean update(DSLContext dsl, CompanyDto company) {
        Condition filterById = COMPANY.ID.eq(company.getId());
        Long headCompanyId = extractHeadCompanyId(company);
        return dsl.update(COMPANY)
                .set(COMPANY.NAME, company.getName())
                .set(COMPANY.HEAD_COMPANY_ID, headCompanyId)
                .where(filterById)
                .execute() > 0;
    }
}
