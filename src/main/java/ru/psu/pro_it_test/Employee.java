package ru.psu.pro_it_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Employee {
    @NonNull
    private long id;
    @NonNull
    private String name;
    private Company company;
    private boolean hasChild;
    private Employee boss;

    public Employee (Long id,
                     String name,
                     Long companyId,
                     String companyName,
                     Long bossId,
                     String bossName) {
        this.id = id;
        this.name = name;
        this.company = companyId == null ? null : new Company(companyId, companyName);
        this.boss = bossId == null ? null : new Employee(bossId, bossName);
    }

    public Employee (long id,
                    String name,
                    boolean isBoss) {
        this.id = id;
        this.name = name;
        this.hasChild = isBoss;
    }
}
