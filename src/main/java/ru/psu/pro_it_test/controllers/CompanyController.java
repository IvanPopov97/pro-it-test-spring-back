package ru.psu.pro_it_test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.psu.pro_it_test.entities.Company;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;
import ru.psu.pro_it_test.repositories.CompanyRepository;

import java.util.List;

@RestController @RequestMapping("company") @CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    private final CompanyRepository repository;

    @Autowired
    public CompanyController(CompanyRepository service) {
        this.repository = service;
    }

    @GetMapping("names")
    public List<Company> getNames() {
        return repository.findAllNames();
    }

    @GetMapping("list")
    public Page<Company> getList(@RequestParam(defaultValue = "0") Long offset,
                                 @RequestParam(defaultValue = "20") int pageSize,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);
        return name == null ? repository.findAll(request) : repository.findByName(name, startsWith, request);
    }

    @GetMapping("tree")
    public List<Company> getTreePart(@RequestParam(required = false) Long parentId) {
        //System.out.println(result);
        return parentId == null ? repository.findRootCompanies() : repository.findDaughters(parentId);
    }

    @GetMapping("count")
    public long getCount(@RequestParam(required = false) String name,
                        @RequestParam(defaultValue = "true") boolean startsWith) {
        return name == null ? repository.findCount() : repository.findCount(name, startsWith);
    }

    @PostMapping
    @ResponseBody
    public long add(@RequestBody Company company) { // возвращает id добавленной компании
        //System.out.println(company.toString());
        return repository.add(company);
    }

}
