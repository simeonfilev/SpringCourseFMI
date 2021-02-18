package com.accounting.easy.service;

import com.accounting.easy.domain.entities.*;

import java.util.*;

public interface CompanyService {

    List<Company> getAllCompaniesWithoutAccountant();

    Long getAllCompaniesSize();

    Optional<Company> findCompanyByUsername(String username);

    void addDocument(String username, Document document);

    List<CompanyEmployee> getAllCompanyEmployees(String company);

    void deleteUserEmployeeWithId(String companyName, String id);

    void removeAccountant(String companyName, String id);

    void removeAccountant(String companyId, Accountant accountant);

    void deleteCompany(Company company);
}
