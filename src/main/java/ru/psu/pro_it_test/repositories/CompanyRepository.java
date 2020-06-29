package ru.psu.pro_it_test.repositories;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
import ru.psu.pro_it_test.entities.Company;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;
//import ru.psu.pro_it_test.tables.records.CompanyRecord;

import java.util.List;
import static org.jooq.impl.DSL.count;
import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;

@Repository
//@Transactional
public class CompanyRepository extends JooqRepository<Company> {

    private final DSLContext dsl;

    private final Field<Integer> EMPLOYEE_COUNT = count(EMPLOYEE.ID).as("employee_count");
    private final Field<Boolean> HAS_CHILD = DSL.field(
            COMPANY.ID.eq(DSL.any(DSL.select(COMPANY.HEAD_COMPANY_ID).from(COMPANY)))
    ).as("has_child");

    private final ru.psu.pro_it_test.tables.Company HEAD = COMPANY.as("head");
    private final Field<?> SORT_FIELD = COMPANY.ID;

    @Autowired
    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    protected SelectJoinStep<?> selectTreeItems() {
        return dsl.select(COMPANY.ID, COMPANY.NAME, HAS_CHILD)
                .from(COMPANY);
    }

    protected SelectJoinStep<?> select(List<Field<?>> fields) {
        return dsl.select(fields)
                .from(COMPANY);
    }

    protected SelectJoinStep<?> selectCount() {
        return dsl.selectCount().from(COMPANY);
    }

    protected SelectJoinStep<?> selectListItems() {
        return dsl.select(COMPANY.ID, COMPANY.NAME, HEAD.ID, HEAD.NAME, EMPLOYEE_COUNT)
                .from(COMPANY)
                .leftJoin(HEAD)
                .on(HEAD.ID.eq(COMPANY.HEAD_COMPANY_ID))
                .leftJoin(EMPLOYEE)
                .on(EMPLOYEE.COMPANY_ID.eq(COMPANY.ID));
    }

    @Override
    protected List<Company> mapToDto(Result<?> result) {
        return result.map(record -> new Company(
                COMPANY.ID.get(record),
                COMPANY.NAME.get(record),
                HEAD.ID.get(record),
                HEAD.NAME.get(record),
                EMPLOYEE_COUNT.get(record)
        ));
    }

    private SelectHavingStep<?> selectJoinAndGroup() {
        return selectListItems().groupBy(COMPANY.ID, HEAD.ID);
    }

    private SelectHavingStep<?> selectJoinFilterAndGroup(Condition filter) {
        return selectListItems().where(filter).groupBy(COMPANY.ID, HEAD.ID);
    }

    public Page<Company> findByName(String name, boolean startsWith, Pageable pageRequest) {

        Condition filter = getFilterByName(COMPANY.NAME, name, startsWith);

        return getPage(selectJoinFilterAndGroup(filter), pageRequest, SORT_FIELD);
    }

    public List<Company> findAllNames() {
        return select(List.of(COMPANY.ID, COMPANY.NAME)).fetchInto(Company.class);
    }

    public Page<Company> findAll(Pageable pageRequest) {
        return getPage(selectJoinAndGroup(), pageRequest, SORT_FIELD);
    }

    public long findCount(String name, boolean startsWith) {
        Condition filter = getFilterByName(COMPANY.NAME, name, startsWith);
        return findCount(filter);
    }

    public List<Company> findDaughters(Long parentId) {
        Condition filter = COMPANY.HEAD_COMPANY_ID.eq(parentId);
        return selectAndFilterTreeItems(filter).fetchInto(Company.class);
    }

    public List<Company> findRootCompanies() {
        Condition filter = COMPANY.HEAD_COMPANY_ID.isNull();
        SelectConditionStep<?> query = selectAndFilterTreeItems(filter);
        return query.fetchInto(Company.class);

    }

    private Long extractHeadCompanyId(Company company) {
        return company.getHeadCompany() == null ? null : company.getHeadCompany().getId();
    }

    public long add(Company company) {
        Long headCompanyID = extractHeadCompanyId(company);
        return dsl.insertInto(COMPANY, COMPANY.NAME, COMPANY.HEAD_COMPANY_ID)
                .values(company.getName(), headCompanyID)
                .returning()
                .fetchOne()
                .getId();
    }

    public boolean remove(long id) {
        Condition filterById = COMPANY.ID.eq(id);
        return dsl.delete(COMPANY).where(filterById).execute() > 0; //execute возвращает количество удалённых строк
    }

    public boolean update(Company company) {
        Condition filterById = COMPANY.ID.eq(company.getId());
        Long headCompanyId = extractHeadCompanyId(company);
        return dsl.update(COMPANY)
                .set(COMPANY.NAME, company.getName())
                .set(COMPANY.HEAD_COMPANY_ID, headCompanyId)
                .where(filterById)
                .execute() > 0;
    }



}
