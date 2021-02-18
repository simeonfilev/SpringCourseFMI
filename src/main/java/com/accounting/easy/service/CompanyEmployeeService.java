package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.CompanyEmployee;

import java.util.List;
import java.util.Optional;

public interface CompanyEmployeeService {
    List<CompanyEmployee> getAllByCompany(Company company);

    Optional<CompanyEmployee> getById(String id);

    Optional<CompanyEmployee> getByUsername(String username);
}
