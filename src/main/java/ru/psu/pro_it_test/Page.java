package ru.psu.pro_it_test;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Page<T> {
    private List<T> content; // контент страницы

    private boolean first; // первая страница (offset > 0) ?
    private boolean last; // если сделать limit + 1, то будет на 1 запись больше?
    private boolean empty; // content size > 0 ?

    //private int number; // как узнать?
    //private int totalElements; // запрос к БД
    //private int totalPages; // вычисляется так: totalElements / pageSize
    //private int numberOfElements;
}
