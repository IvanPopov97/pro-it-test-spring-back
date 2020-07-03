package ru.psu.pro_it_test.repositories;

import ru.psu.pro_it_test.dto.EmployeeDto;

import java.util.List;

public interface EmployeeRepo extends Repo<EmployeeDto, Long> {
    List<EmployeeDto> findNamesByCompanyId(Long companyId);

    List<EmployeeDto> findByCompanyName(String companyName, boolean startsWith, long skip, int limit);

    long findCountByCompanyName(String companyName, boolean startsWith);
}
