package ru.psu.pro_it_test.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDto {
    @NonNull
    private long id;
    @NonNull
    private String name;
    private CompanyDto company;
    private Boolean hasChild;
    private EmployeeDto boss;

    private EmployeeDto() {}
}