package com.accounting.easy.repository;

import com.accounting.easy.domain.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findCompanyByCompanyName(String companyName);

    List<Company> findAllByAccountantIsNull();

    Optional<Company> findCompanyByUsername(String username);
}
