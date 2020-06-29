package ru.psu.pro_it_test.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // "неизвестные" свойства присланного объекта игнорятся
public class Company {
    @NonNull
    private long id;
    @NonNull
    private String name;
    private long employeeCount;
    private Company headCompany;
    private boolean hasChild;

    private Company() {}

    public Company (long id,
                    String name,
                    Long headCompanyId,
                    String headCompanyName,
                    long employeeCount) {
        this.id = id;
        this.name = name;
        this.employeeCount = employeeCount;
        this.headCompany = headCompanyId == null ? null : new Company(headCompanyId, headCompanyName);
    }

    public Company (long id,
                    String name,
                    boolean hasChild) {
        this.id = id;
        this.name = name;
        this.hasChild = hasChild;
    }
}