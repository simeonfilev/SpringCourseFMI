package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Accountant;
import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.repository.AccountantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountantServiceImpl implements AccountantService {
    private final AccountantRepository accountantRepository;

    public AccountantServiceImpl(AccountantRepository accountantRepository) {
        this.accountantRepository = accountantRepository;
    }

    @Override
    public List<Accountant> getAllAccountants() {
        return this.accountantRepository.findAll();
    }

    @Override
    public Optional<Accountant> getByUsername(String username) {

        return this.accountantRepository.findByUsername(username);
    }

    @Override
    public void removeCompany(Accountant accountant, Company company) {
        accountant.getCompanies().remove(company);
        this.accountantRepository.save(accountant);
        this.accountantRepository.flush();
    }

}
