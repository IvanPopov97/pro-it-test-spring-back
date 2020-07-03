package ru.psu.pro_it_test.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.psu.pro_it_test.dto.EmployeeDto;
import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;
import ru.psu.pro_it_test.exceptions.CannotBeDeletedException;
import ru.psu.pro_it_test.repositories.DefaultEmployeeRepo;
import ru.psu.pro_it_test.repositories.EmployeeRepo;

import java.util.List;

@org.springframework.stereotype.Service
public class DefaultEmployeeService implements Service<EmployeeDto, Long>, EmployeeService {


    private final EmployeeRepo repo;

    public DefaultEmployeeService(DefaultEmployeeRepo repo) {
        this.repo = repo;
    }

    @Override
    public Page<EmployeeDto> findAll(Pageable request) {
        List<EmployeeDto> items = repo.findAll(request.getOffset(), request.getPageSize() + 1);
        return createPageWithoutLastItem(items, request);
    }

    @Override
    public Page<EmployeeDto> findByName(String name, boolean startsWith, Pageable request) {
        List<EmployeeDto> items = repo.findByName(
                name,
                startsWith,
                request.getOffset(),
                request.getPageSize() + 1
        );
        return createPageWithoutLastItem(items, request);
    }

    @Override
    public Page<EmployeeDto> findByCompanyName(String companyName, boolean startsWith, Pageable request) {
        List<EmployeeDto> items = repo.findByCompanyName(
                companyName,
                startsWith,
                request.getOffset(),
                request.getPageSize() + 1
        );
        return createPageWithoutLastItem(items, request);
    }

    @Override
    public List<EmployeeDto> findNamesByCompanyId(Long companyId) {
        return repo.findNamesByCompanyId(companyId);
    }

    @Override
    public List<EmployeeDto> findAllNames() {
        return repo.findAll(true);
    }

    @Override
    public List<EmployeeDto> findRootItems() {
        return repo.findRootItems();
    }

    @Override
    public List<EmployeeDto> findChildItems(Long parentId) {
        return repo.findChildItems(parentId);
    }

    @Override
    public long findCount() {
        return repo.findCount();
    }

    @Override
    public long findCount(String name, boolean startsWith) {
        return repo.findCount(name, startsWith);
    }

    @Override
    public long findCountByCompanyName(String companyName, boolean startsWith) {
        return repo.findCountByCompanyName(companyName, startsWith);
    }

    @Override
    @Transactional
    public EmployeeDto add(EmployeeDto item) {
        return repo.add(item);
    }

    @Override
    @Transactional
    public boolean remove(Long itemId) {
        try {
            return repo.remove(itemId);
        } catch (DataIntegrityViolationException e) {
            throw new CannotBeDeletedException("Невозможно удалить работника, т.к. у него есть подчинённые");
        }
    }

    @Override
    @Transactional
    public boolean update(EmployeeDto item) {
        return repo.update(item);
    }
}
