package ru.psu.pro_it_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("employee") @CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    private final EmployeeRepository repository;

    @Autowired
    public EmployeeController(EmployeeRepository service) {
        this.repository = service;
    }

    @GetMapping("list")
    public Page<Employee> getList(@RequestParam(defaultValue = "0") Long offset,
                                @RequestParam(defaultValue = "20") int pageSize,
                                @RequestParam(required = false) String name,
                                @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);

        return name == null ? repository.findAll(request) : repository.findByName(name, startsWith, request);
    }

    @GetMapping("tree")
    public List<Employee> getTreePart(@RequestParam(required = false) Long parentId) {
        return parentId == null ? repository.findHigherBosses() : repository.findSubordinates(parentId);
    }

    @GetMapping("count")
    public long getCount(@RequestParam(required = false) String name,
                        @RequestParam(defaultValue = "true") boolean startsWith) {
        return name == null ? repository.findCount() : repository.findCount(name, startsWith);
    }
}
