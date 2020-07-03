package ru.psu.pro_it_test.services;

import ru.psu.pro_it_test.dto.EmployeeDto;
import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;

import java.util.List;

public interface EmployeeService extends Service<EmployeeDto, Long> {

    Page<EmployeeDto> findByCompanyName(String companyName, boolean startsWith, Pageable request);

    List<EmployeeDto> findNamesByCompanyId(Long companyId);

    long findCountByCompanyName(String companyName, boolean startsWith);

}
