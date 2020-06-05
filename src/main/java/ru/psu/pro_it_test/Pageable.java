package ru.psu.pro_it_test;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pageable {
    private int offset;
    private int pageSize;
    //private int pageNumber;
}
