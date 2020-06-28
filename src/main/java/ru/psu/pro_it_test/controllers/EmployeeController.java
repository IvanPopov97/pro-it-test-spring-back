package ru.psu.pro_it_test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import ru.psu.pro_it_test.entities.Company;
import ru.psu.pro_it_test.entities.Employee;
import ru.psu.pro_it_test.entities.Page;
import ru.psu.pro_it_test.entities.Pageable;
import ru.psu.pro_it_test.repositories.EmployeeRepository;

import java.util.List;

@RestController @RequestMapping("api/employee") @CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    private final EmployeeRepository repository;

    @Autowired
    public EmployeeController(EmployeeRepository service) {
        this.repository = service;
    }

    @GetMapping("names")
    public List<Employee> getNames(long companyId) {
        return repository.findNamesByCompanyId(companyId);
    }

    @GetMapping("list")
    public Page<Employee> getList(@RequestParam(defaultValue = "0") Long offset,
                                  @RequestParam(defaultValue = "20") int pageSize,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String companyName,
                                  @RequestParam(defaultValue = "true") boolean isEmployeeName,
                                  @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);

        return name == null
                ? repository.findAll(request)
                : companyName == null
                ? repository.findByName(name, isEmployeeName, startsWith, request)
                : repository.findByName(name, companyName, startsWith, request);
    }

    @GetMapping("tree")
    public List<Employee> getTreePart(@RequestParam(required = false) Long parentId) {
        return parentId == null ? repository.findRootEmployees() : repository.findSubordinates(parentId);
    }

    @GetMapping("count")
    public long getCount(@RequestParam(required = false) String name,
                         @RequestParam(required = false) String companyName,
                         @RequestParam(defaultValue = "true") boolean isEmployeeName,
                         @RequestParam(defaultValue = "true") boolean startsWith) {

        return name == null
                ? repository.findCount()
                : companyName == null
                ? repository.findCount(name, isEmployeeName, startsWith)
                : repository.findCount(name, companyName, startsWith);
    }

    @PostMapping
    @ResponseBody
    public long add(@RequestBody Employee employee) {
        System.out.println(employee);
        return repository.add(employee);
    }

    @PutMapping("{id}")
    public Employee update(@PathVariable long id, @RequestBody Employee employee) {
        employee.setId(id);
        //repository.update(employee);
        System.out.println(employee);
        return employee;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        System.out.println(id);
        repository.remove(id);
    }
}
