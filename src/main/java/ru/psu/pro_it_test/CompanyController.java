package ru.psu.pro_it_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("company")
public class CompanyController {

    private final CompanyRepository repository;

    @Autowired
    public CompanyController(CompanyRepository service) {
        this.repository = service;
    }

    @GetMapping("list")
    public List<Company> getAll(@RequestParam(required = false) String name,
                                @RequestParam(defaultValue = "true") boolean startsWith) {
        if (name == null)
            return repository.findAll();
        else
            return repository.findAllByName(name, startsWith);
    }

}
