package ru.psu.pro_it_test.services;

import org.springframework.transaction.annotation.Transactional;
import ru.psu.pro_it_test.dto.CompanyDto;
import ru.psu.pro_it_test.dto.Page;
import ru.psu.pro_it_test.dto.Pageable;
import ru.psu.pro_it_test.exceptions.CannotBeDeletedException;
import ru.psu.pro_it_test.repositories.Repo;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@org.springframework.stereotype.Service
public class DefaultCompanyService implements Service<CompanyDto, Long>, CompanyService {

    private final Repo<CompanyDto, Long> repo;

    public DefaultCompanyService(Repo<CompanyDto, Long> repo) {
        this.repo = repo;
    }

    @Override
    public Page<CompanyDto> findAll(Pageable request) {
        List<CompanyDto> items = repo.findAll(request.getOffset(), request.getPageSize() + 1);
        return createPageWithoutLastItem(items, request);
    }

    @Override
    public Page<CompanyDto> findByName(String name, boolean startsWith, Pageable request) {
        List<CompanyDto> items = repo.findByName(
                name,
                startsWith,
                request.getOffset(),
                request.getPageSize() + 1);
        return createPageWithoutLastItem(items, request);
    }

    @Override
    public List<CompanyDto> findAllNames() {
        return repo.findAll(true);
    }

    @Override
    public List<CompanyDto> findRootItems() {
        return repo.findRootItems();
    }

    @Override
    public List<CompanyDto> findChildItems(Long parentId) {
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
    @Transactional
    public CompanyDto add(CompanyDto item) {
        return repo.add(item);
    }

    @Override
    @Transactional
    public boolean remove(Long itemId) {
        try {
            return repo.remove(itemId);
        } catch (DataIntegrityViolationException e) {
            throw new CannotBeDeletedException("Невозможно удалить компанию, т.к. у неё есть дочерние элементы");
        }
    }

    @Override
    @Transactional
    public boolean update(CompanyDto item) {
        return repo.update(item);
    }
}
