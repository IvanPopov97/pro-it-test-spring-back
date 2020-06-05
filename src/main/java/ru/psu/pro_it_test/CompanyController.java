package ru.psu.pro_it_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("company")
public class CompanyController {

    private final CompanyRepository repository;

    @Autowired
    public CompanyController(CompanyRepository service) {
        this.repository = service;
    }

    @GetMapping("list")
    public List<Company> getAll(@RequestParam(defaultValue = "0") Integer offset,
                                @RequestParam(defaultValue = "20") Integer pageSize,
                                @RequestParam(required = false) String name,
                                @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);

        return name == null ? repository.findAll(request) : repository.findByName(name, startsWith, request);
    }

}
