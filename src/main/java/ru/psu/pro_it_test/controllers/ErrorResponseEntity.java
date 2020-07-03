package ru.psu.pro_it_test.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseEntity {
    private String message;
    private String error;
    private int status;
}