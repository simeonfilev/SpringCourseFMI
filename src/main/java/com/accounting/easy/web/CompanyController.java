package com.accounting.easy.web;

import com.accounting.easy.domain.entities.Accountant;
import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.Request;
import com.accounting.easy.service.AccountantService;
import com.accounting.easy.service.CompanyService;
import com.accounting.easy.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping()
public class CompanyController extends BaseController  {

    private final CompanyService companyService;
    private final AccountantService accountantService;
    private final RequestService requestService;

    @Autowired
    public CompanyController(CompanyService companyService, AccountantService accountantService, RequestService requestService) {
        this.companyService = companyService;
        this.accountantService = accountantService;
        this.requestService = requestService;
    }

    @GetMapping(path = "/api/company")
    public List<Company> getAllCompaniesWithoutAcccountant(){
        return companyService.getAllCompaniesWithoutAccountant();
    }

    @PostMapping(path = "/api/company")
    public ModelAndView createCompany(){
        return redirect("/");
    };

    @GetMapping(path = "/findAccountant")
    public ModelAndView findAccountant(ModelAndView modelAndView){
        List<Accountant> accountants =  this.accountantService.getAllAccountants();
        modelAndView.addObject("accountants",accountants);
        return this.view("accountants", modelAndView);
    }

    @GetMapping(path = "/findCompany")
    public ModelAndView findCompany(ModelAndView modelAndView){
        List<Company> companiesWithoutAccountant =  this.companyService.getAllCompaniesWithoutAccountant();
        modelAndView.addObject("companies",companiesWithoutAccountant);
        return this.view("companies", modelAndView);
    }

    @GetMapping(path = "/requests")
    public ModelAndView getRequests(ModelAndView modelAndView, Authentication authentication){
        String username = authentication.getName();
        List<Request> requests =  this.requestService.getAllRequestsForToUsername(username);
        modelAndView.addObject("requests",requests);
        return this.view("requests", modelAndView);
    }

    @PostMapping(path = "/applyForAccountant/{username}")
    public ModelAndView applyForAccountant(ModelAndView modelAndView, @PathVariable("username") String toUsername, Authentication authentication){
        String fromUsername = authentication.getName();
        this.requestService.createRequest(fromUsername,toUsername);
        return this.redirect("/");
    }

    @PostMapping(path = "/applyForCompany/{username}")
    public ModelAndView applyForCompany(ModelAndView modelAndView, @PathVariable("username") String toUsername, Authentication authentication){
        String fromUsername = authentication.getName();
        this.requestService.createRequest(fromUsername,toUsername);
        return this.redirect("/");
    }

    @PostMapping(path = "/acceptRequest/{requestId}")
    public ModelAndView acceptRequest(ModelAndView modelAndView, @PathVariable("requestId") String requestId, Authentication authentication){
        String fromUsername = authentication.getName();
        this.requestService.acceptRequest(requestId);
        return this.redirect("/");
    }

    @PostMapping(path = "/deleteRequest/{requestId}")
    public ModelAndView deleteRequest(@PathVariable("requestId") String requestId, Authentication authentication){
        String fromUsername = authentication.getName();
        this.requestService.deleteRequest(requestId,fromUsername);
        return this.redirect("/requests");
    }

    @GetMapping(path = "/myCompany")
    public ModelAndView myCompany(ModelAndView modelAndView, Authentication authentication){
        String fromUsername = authentication.getName();
        Optional<Company> company = this.companyService.findCompanyByUsername(fromUsername);
        if(company.isPresent()){
            modelAndView.addObject("employees", companyService.getAllCompanyEmployees(fromUsername));
            modelAndView.addObject("company", companyService.findCompanyByUsername(fromUsername).get());
            modelAndView.addObject("accountant", companyService.findCompanyByUsername(fromUsername).get().getAccountant());
        }
        return this.view("/company",modelAndView);
    }

    @GetMapping(path = "/accountantCompanies")
    public ModelAndView getAccountant(ModelAndView modelAndView, Authentication authentication){
        String username = authentication.getName();
        Optional<Accountant> accountant = this.accountantService.getByUsername(username);
        if(accountant.isPresent()){
            modelAndView.addObject("companies", accountant.get().getCompanies());
            modelAndView.addObject("accountant", accountant.get());
        }
        return this.view("/accountant",modelAndView);
    }

    @PostMapping(path = "/deleteUserFromCompany/{userId}")
    public ModelAndView deleteUserFromCompany(@PathVariable("userId") String userId, Authentication authentication){
        String fromUsername = authentication.getName();
        this.companyService.deleteUserEmployeeWithId(fromUsername,userId);
        return this.redirect("/myCompany");
    }

    @PostMapping(path = "/removeAccountantFromCompany/{userId}")
    public ModelAndView removeAccountantFromCompany(@PathVariable("userId") String userId, Authentication authentication){
        String fromUsername = authentication.getName();
        this.companyService.removeAccountant(fromUsername,userId);
        return this.redirect("/myCompany");
    }

    @PostMapping(path = "/deleteCompanyFromAccountant/{companyId}")
    public ModelAndView removeCompanyFromAccountant(@PathVariable("companyId") String companyId, Authentication authentication){
        String accountantUsername = authentication.getName();
        Optional<Accountant> accountant = this.accountantService.getByUsername(accountantUsername);
        accountant.ifPresent(value -> this.companyService.removeAccountant(companyId, value));

        return this.redirect("/accountantCompanies");
    }

}
