package ru.psu.pro_it_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {
    private long offset;
    private int pageSize;
    //private int pageNumber;
}
