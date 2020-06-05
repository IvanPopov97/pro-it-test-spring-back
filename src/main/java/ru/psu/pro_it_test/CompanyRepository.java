package ru.psu.pro_it_test;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ru.psu.pro_it_test.tables.Company.COMPANY;
import static ru.psu.pro_it_test.tables.Employee.EMPLOYEE;


@Repository
@Transactional
public class CompanyRepository {

    private final DSLContext dsl;

    @Autowired
    public CompanyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<Company> findAll() {
        return findAllByName("", true);
    }

    private String getRegex(String name, boolean startsWith) {
        return startsWith ? name + "%" : name;
    }

    private Condition getFilter(String name, boolean startsWith) {
        return COMPANY.NAME.likeIgnoreCase(getRegex(name, startsWith));
    }

    public int findCount(String name, boolean startsWith) {
        SelectJoinStep<?> afterSelect = dsl.selectCount().from(COMPANY);
        return name.equals("") ?
                afterSelect.fetchOne(0, int.class) :
                afterSelect.where(getFilter(name, startsWith)).fetchOne(0, int.class);
    }

    public List<Company> findAllByName(String name, boolean startsWith) {
        AggregateFunction<Integer> count = DSL.count(EMPLOYEE.ID);
        ru.psu.pro_it_test.tables.Company HEAD = COMPANY.as("head");

        SelectJoinStep<?> afterJoin = dsl.select(COMPANY.ID, COMPANY.NAME, HEAD.ID, HEAD.NAME, count)
                .from(COMPANY)
                .leftJoin(HEAD)
                .on(HEAD.ID.eq(COMPANY.HEAD_COMPANY_ID))
                .leftJoin(EMPLOYEE)
                .on(EMPLOYEE.COMPANY_ID.eq(COMPANY.ID));

        SelectConnectByStep<?> afterWhere = name.equals("") ?
                afterJoin :
                afterJoin.where(getFilter(name, startsWith));

        System.out.println(afterWhere.getSQL());

        return afterWhere
                .groupBy(COMPANY.ID, HEAD.ID)
                .fetch()
                .map(record -> new Company(
                        COMPANY.ID.get(record),
                        COMPANY.NAME.get(record),
                        HEAD.ID.get(record),
                        HEAD.NAME.get(record),
                        count.get(record)
                ));
    }
}
