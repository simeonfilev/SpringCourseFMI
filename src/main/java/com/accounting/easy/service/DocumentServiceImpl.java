package com.accounting.easy.service;

import com.accounting.easy.domain.entities.*;
import com.accounting.easy.repository.CompanyEmployeeRepository;
import com.accounting.easy.repository.CompanyRepository;
import com.accounting.easy.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService{


    private final DocumentRepository documentRepository;
    private final CompanyRepository companyRepository;
    private final AccountantService accountantService;
    private final CompanyEmployeeRepository companyEmployeeRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, CompanyRepository companyRepository, AccountantService accountantService, CompanyEmployeeRepository companyEmployeeRepository) {
        this.documentRepository = documentRepository;
        this.companyRepository = companyRepository;
        this.accountantService = accountantService;
        this.companyEmployeeRepository = companyEmployeeRepository;
    }


    @Override
    public List<Document> getAllDocumentsBasedOnCompany(Company company) {
        List<Document> documents = documentRepository.findAllByCompany(company);

        return documents;
    }

    @Override
    public Long getAllDocumentsSize() {
        return (long) documentRepository.findAll().size();
    }

    @Override
    public List<Document> getAllDocudemntsByUsername(String user) {
        return this.documentRepository.findAllByUserId(user);
    }

    @Override
    public List<Document> getAllDocudemntsByUser(User user) {

        if(user.getRole().equals(Roles.ROLE_COMPANY)){
            var company = this.companyRepository.findCompanyByUsername(user.getUsername());
            if(company.isPresent()){
                return this.documentRepository.findAllByCompany(company.get());
            }else{
                return new ArrayList<>();
            }
        }else if(user.getRole().equals(Roles.ROLE_COMPANY_USER)){
            return this.documentRepository.findAllByUserId(user.getId());
        }else if(user.getRole().equals(Roles.ROLE_ACCOUNTANT)){
            Optional<Accountant> accountant = this.accountantService.getByUsername(user.getUsername());

            if(accountant.isPresent()){
                List<Company> companies = accountant.get().getCompanies();
                List<Document> documents = new ArrayList<>();

                for(Company company : companies){
                    List<Document> documentsForCompany = documentRepository.findAllByCompany(company);
                    documents.addAll(documentsForCompany);
                }

                return documents;
            }else{
                return new ArrayList<>();
            }
        }

        return null;
    }

    @Override
    public List<Document> getAllDocuments() {
        return this.documentRepository.findAll();
    }

    @Override
    public void deleteDocumentById(String id, User user) {
        Optional<Document> optDocument = this.documentRepository.findById(id);
        if(!optDocument.isPresent()){
            return;
        }
        Document doc = optDocument.get();

        if(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
            this.documentRepository.deleteById(id);
            this.documentRepository.flush();
        } else if(user.getRole().equals(Roles.ROLE_COMPANY)){
            Optional<Company> companyTemp = this.companyRepository.findCompanyByUsername(user.getUsername());
            if(!companyTemp.isPresent()){
                return;
            }
            Company company = companyTemp.get();
            if(company.getId().equals(doc.getUserId())){
                this.documentRepository.deleteById(id);
                this.documentRepository.flush();
            }
        }else if(user.getRole().equals(Roles.ROLE_COMPANY_USER)){
            Optional<CompanyEmployee> tempEmployee = this.companyEmployeeRepository.findByUsername(user.getUsername());
            if(!tempEmployee.isPresent()){
                return;
            }
            CompanyEmployee employee = tempEmployee.get();
            if(user.getId().equals(doc.getUserId())){
                this.documentRepository.deleteById(id);
                this.documentRepository.flush();
            }
        }else if(user.getRole().equals(Roles.ROLE_ACCOUNTANT)){
            Optional<Accountant> tempAcc = this.accountantService.getByUsername(user.getUsername());
            if(!tempAcc.isPresent()){
                return;
            }
            Accountant accountant = tempAcc.get();
            if(accountant.getId().equals(doc.getUserId())
                    || doc.getCompany().getAccountant().getId().equals(accountant.getId())){
                this.documentRepository.deleteById(id);
                this.documentRepository.flush();
            }
        }

    }

    @Override
    public Flux<Document> findAll() {
        return Flux.fromIterable(this.documentRepository.findAll());
    }

    @Override
    public Flux<Document> getAllDocudemntsByUserFlux(User user) {
        return Flux.fromIterable(this.getAllDocudemntsByUser(user));
    }
}
