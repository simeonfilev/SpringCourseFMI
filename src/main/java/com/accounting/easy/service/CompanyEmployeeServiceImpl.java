package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.CompanyEmployee;
import com.accounting.easy.repository.CompanyEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyEmployeeServiceImpl implements CompanyEmployeeService{

    private final CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    public CompanyEmployeeServiceImpl(CompanyEmployeeRepository companyEmployeeRepository) {
        this.companyEmployeeRepository = companyEmployeeRepository;
    }

    @Override
    public List<CompanyEmployee> getAllByCompany(Company company) {
        return this.companyEmployeeRepository.findAllByCompany(company);
    }

    @Override
    public Optional<CompanyEmployee> getById(String id) {
        return this.companyEmployeeRepository.findById(id);
    }

    @Override
    public Optional<CompanyEmployee> getByUsername(String username) {
        return this.companyEmployeeRepository.findByUsername(username);
    }
}
