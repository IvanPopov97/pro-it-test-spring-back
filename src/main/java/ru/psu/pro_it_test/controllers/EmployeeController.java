package ru.psu.pro_it_test.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import ru.psu.pro_it_test.entities.Company;
import ru.psu.pro_it_test.dto.EmployeeDto;
import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;
import ru.psu.pro_it_test.services.EmployeeService;

import java.util.List;

@RestController @RequestMapping("api/employee") @CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {

    private final EmployeeService service;

    @Autowired
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("names")
    public List<EmployeeDto> getNames(Long companyId) {
        return service.findNamesByCompanyId(companyId);
    }

    @GetMapping("list")
    public Page<EmployeeDto> getList(@RequestParam(defaultValue = "0") Long offset,
                                     @RequestParam(defaultValue = "20") int pageSize,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(defaultValue = "true") boolean isEmployeeName,
                                     @RequestParam(defaultValue = "true") boolean startsWith) {

        Pageable request = new Pageable(offset, pageSize);

        return name == null
                ? service.findAll(request)
                : isEmployeeName
                ? service.findByName(name, startsWith, request)
                : service.findByCompanyName(name, startsWith, request);
    }

    @GetMapping("tree")
    public List<EmployeeDto> getTreePart(@RequestParam(required = false) Long parentId) {
        return parentId == null ? service.findRootItems() : service.findChildItems(parentId);
    }

    @GetMapping("count")
    public long getCount(@RequestParam(required = false) String name,
                         @RequestParam(defaultValue = "true") boolean isEmployeeName,
                         @RequestParam(defaultValue = "true") boolean startsWith) {

        return name == null
                ? service.findCount()
                : isEmployeeName
                ? service.findCount(name, startsWith)
                : service.findCountByCompanyName(name, startsWith);
    }

    @PostMapping
    @ResponseBody
    public long add(@RequestBody EmployeeDto employee) {
        return service.add(employee).getId();
    }

    @PutMapping("{id}")
    public EmployeeDto update(@PathVariable long id, @RequestBody EmployeeDto employee) {
        employee.setId(id);
        service.update(employee);
        return employee;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.remove(id);
    }
}
