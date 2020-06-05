package ru.psu.pro_it_test;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class Company {
    @NonNull
    private long id;
    @NonNull
    private String name;
    private Company headCompany;
    private int employeeCount;

    public Company (Long id,
                    String name,
                    Long headCompanyId,
                    String headCompanyName,
                    Integer employeeCount) {
        this.id = id;
        this.name = name;
        this.headCompany = headCompanyId == null ? null : new Company(headCompanyId, headCompanyName);
        this.employeeCount = employeeCount;
    }
}