package com.accounting.easy.service;

import com.accounting.easy.domain.entities.*;
import com.accounting.easy.repository.AccountantRepository;
import com.accounting.easy.repository.CompanyRepository;
import com.accounting.easy.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final AccountantRepository accountantRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserService userService, CompanyRepository companyRepository, AccountantRepository accountantRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.accountantRepository = accountantRepository;
    }


    @Override
    public List<Request> getAllRequestsForToUsername(String username) {
        return this.requestRepository.findAllByToUsername(username);
    }

    @Override
    public void createRequest(String fromUsername, String toUsername) {
        Request request = new Request();
        request.setFromUsername(fromUsername);
        request.setToUsername(toUsername);

        this.requestRepository.saveAndFlush(request);
    }

    @Override
    public void acceptRequest(String id) {
        Optional<Request> request =   this.requestRepository.findById(id);
        if(!request.isPresent()){
            return;
        }
        String fromUsername = request.get().getFromUsername();
        String toUsername = request.get().getToUsername();

        Optional<User> fromUser = this.userService.findByUsername(fromUsername);
        Optional<User> toUser = this.userService.findByUsername(toUsername);

        if(!fromUser.isPresent() || !toUser.isPresent()){
            return;
        }

        //type 1 from user is Company and type 2 is Accountant

        if(fromUser.get().getRole().equals(Roles.ROLE_COMPANY) && toUser.get().getRole().equals(Roles.ROLE_ACCOUNTANT)){
            Optional<Company> company = this.companyRepository.findCompanyByUsername(fromUser.get().getUsername());
            Optional<Accountant> accountant = this.accountantRepository.findByUsername(toUser.get().getUsername());

            if(company.get().getAccountant() == null){
                company.get().setAccountant(accountant.get());
                companyRepository.saveAndFlush(company.get());

                List<Company> temp = accountant.get().getCompanies();
                temp.add(company.get());
                accountant.get().setCompanies(temp);
                accountantRepository.saveAndFlush(accountant.get());
            }
        }else if(fromUser.get().getRole().equals(Roles.ROLE_ACCOUNTANT) && toUser.get().getRole().equals(Roles.ROLE_COMPANY)){
            Optional<Company> company = this.companyRepository.findCompanyByUsername(toUser.get().getUsername());
            Optional<Accountant> accountant = this.accountantRepository.findByUsername(fromUser.get().getUsername());

            if(company.get().getAccountant() == null){
                company.get().setAccountant(accountant.get());
                companyRepository.saveAndFlush(company.get());

                List<Company> temp = accountant.get().getCompanies();
                temp.add(company.get());
                accountant.get().setCompanies(temp);
                accountantRepository.saveAndFlush(accountant.get());
            }
        }
    }

    @Override
    public void deleteRequest(String id, String username) {
        Optional<Request> request =   this.requestRepository.findById(id);
        if(!request.isPresent()){
            return;
        }
        if(username.equals(request.get().getToUsername())){
            this.requestRepository.delete(request.get());
            this.requestRepository.flush();
        }
    }
}
