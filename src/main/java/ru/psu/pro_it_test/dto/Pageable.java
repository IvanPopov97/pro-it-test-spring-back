package ru.psu.pro_it_test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Pageable {
    private long offset;
    private int pageSize;

    private Pageable() {}

    //private int pageNumber;
}
