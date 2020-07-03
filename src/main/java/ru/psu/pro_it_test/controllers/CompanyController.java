package ru.psu.pro_it_test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
import ru.psu.pro_it_test.dto.CompanyDto;
import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;
//import ru.psu.pro_it_test.exceptions.CannotBeDeletedException;
import ru.psu.pro_it_test.services.CompanyService;

import java.util.List;

@RestController @RequestMapping("api/company") @CrossOrigin(origins = "http://localhost:3000")
public class CompanyController {

    private final CompanyService service;

    @Autowired
    public CompanyController(CompanyService service) {
        this.service = service;
    }



    @GetMapping("names")
    public List<CompanyDto> getNames() {
        return service.findAllNames();
    }

    @GetMapping("list")
    public Page<CompanyDto> getList(@RequestParam(defaultValue = "0") Long offset,
                                    @RequestParam(defaultValue = "20") int pageSize,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);
        return name == null ? service.findAll(request) : service.findByName(name, startsWith, request);
    }

    @GetMapping("tree")
    public List<CompanyDto> getTreePart(@RequestParam(required = false) Long parentId) {
        return parentId == null ? service.findRootItems() : service.findChildItems(parentId);
    }

    @GetMapping("count")
    public long getCount(@RequestParam(required = false) String name,
                        @RequestParam(defaultValue = "true") boolean startsWith) {
        return name == null ? service.findCount() : service.findCount(name, startsWith);
    }

    @PostMapping
    @ResponseBody
    public long add(@RequestBody CompanyDto company) { // возвращает id добавленной компании
        return service.add(company).getId();
    }

    @PutMapping("{id}")
    public CompanyDto update(@PathVariable long id, @RequestBody CompanyDto company) {
        company.setId(id);
        service.update(company);
        return company;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.remove(id);
    }
}
