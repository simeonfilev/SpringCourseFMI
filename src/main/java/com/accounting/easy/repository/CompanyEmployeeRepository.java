package com.accounting.easy.repository;

import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, String> {
        List<CompanyEmployee> findAllByCompany(Company company);

        Optional<CompanyEmployee> findById(String id);

        Optional<CompanyEmployee> findByUsername(String username);
}


