package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Accountant;
import com.accounting.easy.domain.entities.Company;

import java.util.List;
import java.util.Optional;

public interface AccountantService {
    List<Accountant> getAllAccountants();

    Optional<Accountant> getByUsername(String username);

    void removeCompany(Accountant accountant, Company company);
}
