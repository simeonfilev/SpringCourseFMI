package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Accountant;
import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.CompanyEmployee;
import com.accounting.easy.domain.entities.Document;
import com.accounting.easy.repository.CompanyEmployeeRepository;
import com.accounting.easy.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyEmployeeRepository companyEmployeeRepository;
    private final AccountantService accountantService;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyEmployeeRepository companyEmployeeRepository, AccountantService accountantService) {
        this.companyRepository = companyRepository;
        this.companyEmployeeRepository = companyEmployeeRepository;
        this.accountantService = accountantService;
    }

    @Override
    public List<Company> getAllCompaniesWithoutAccountant() {
        return companyRepository.findAllByAccountantIsNull();
    }

    @Override
    public Long getAllCompaniesSize() {
        return (long) this.companyRepository.findAll().size();
    }

    @Override
    public Optional<Company> findCompanyByUsername(String username) {
        return this.companyRepository.findCompanyByUsername(username);
    }

    @Override
    public void addDocument(String username, Document document) {
        Optional<Company> company = findCompanyByUsername( username);
        if(company.isPresent()){
            Company tempCompany = company.get();
            List<Document> previousDocuments = tempCompany.getDocuments();
            previousDocuments.add(document);
            tempCompany.setDocuments(previousDocuments);
            this.companyRepository.saveAndFlush(tempCompany);
        }
    }

    @Override
    public List<CompanyEmployee> getAllCompanyEmployees(String username) {
        Optional<Company> company = findCompanyByUsername( username);
        return company.isPresent() ? company.get().getEmployees() : new ArrayList<>();
    }

    @Override
    public void deleteUserEmployeeWithId(String companyName, String id) {
        Optional<Company> company = findCompanyByUsername( companyName);
        if(!company.isPresent()){
            return;
        }
        for(int i=0;i<company.get().getEmployees().size();i++){
            CompanyEmployee temp = company.get().getEmployees().get(i);
            if(temp.getId().equals(id)){
                company.get().getEmployees().remove(temp);
                this.companyEmployeeRepository.delete(temp);
                this.companyEmployeeRepository.flush();
            }
        }
    }

    @Override
    public void removeAccountant(String companyName, String id) {
        Optional<Company> company = findCompanyByUsername( companyName);
        if(!company.isPresent()){
            return;
        }
        if(company.get().getAccountant() == null){
            return;
        }
        Accountant accountant = company.get().getAccountant();
        this.accountantService.removeCompany(accountant,company.get());
        company.get().setAccountant(null);
        this.companyRepository.saveAndFlush(company.get());
    }


    @Override
    public void removeAccountant(String companyId, Accountant accountant) {
        Optional<Company> company = this.companyRepository.findById(companyId);
        if(!company.isPresent()){
            return;
        }
        if(company.get().getAccountant() == null){
            return;
        }
        this.accountantService.removeCompany(accountant,company.get());
        company.get().setAccountant(null);
        this.companyRepository.saveAndFlush(company.get());
    }

    @Override
    public void deleteCompany(Company company) {
        List<CompanyEmployee> employees = company.getEmployees();
        for (CompanyEmployee employee: employees) {
            this.deleteUserEmployeeWithId(company.getUsername() ,employee.getId());
            this.removeAccountant(company.getUsername(),company.getAccountant());
        }
        this.companyRepository.delete(company);
        this.companyRepository.flush();
    }
}
