package com.accounting.easy.web;


import com.accounting.easy.domain.entities.Roles;
import com.accounting.easy.domain.models.binding.UserRegisterBindingModel;
import com.accounting.easy.domain.models.service.CompanyEmployeeServiceModel;
import com.accounting.easy.domain.models.service.CompanyServiceModel;
import com.accounting.easy.domain.models.service.AccountantServiceModel;
import com.accounting.easy.service.EmailService;
import com.accounting.easy.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController extends BaseController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, EmailService emailService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        return this.view("login");
    }


    @GetMapping("/register")
    public ModelAndView register() {
        return this.view("register");
    }

    @PostMapping("/register")
    public ModelAndView registerPost(@ModelAttribute @Valid UserRegisterBindingModel userRegisterBindingModel, BindingResult bindingResult) {
        //check for errors and if there are some errors return them to the user for displaying them
        if (this.userService.alreadyExistByEmail(userRegisterBindingModel.getEmail()) || (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) ||
                this.userService.alreadyExistByUsername(userRegisterBindingModel.getUsername()) || bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            if (this.userService.alreadyExistByUsername(userRegisterBindingModel.getUsername())) {
                modelAndView.addObject("usernameAlready", true);
            }
            if (this.userService.alreadyExistByEmail(userRegisterBindingModel.getEmail())) {
                modelAndView.addObject("emailAlready", true);
            }
            if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
                modelAndView.addObject("match", true);
            }
            if (bindingResult.hasErrors()) {
                modelAndView.addObject("errors", bindingResult.getAllErrors());
            }
            return this.view("register", modelAndView);
        }else{
            if(userRegisterBindingModel.getType().equals(Roles.ROLE_COMPANY.toString())){
                this.userService.createUser(this.modelMapper.map(userRegisterBindingModel, CompanyServiceModel.class));
            }else if(userRegisterBindingModel.getType().equals(Roles.ROLE_ACCOUNTANT.toString())){
                this.userService.createUser(this.modelMapper.map(userRegisterBindingModel, AccountantServiceModel.class));
            }else if(userRegisterBindingModel.getType().equals(Roles.ROLE_COMPANY_USER.toString())){
                this.userService.createUser(this.modelMapper.map(userRegisterBindingModel, CompanyEmployeeServiceModel.class));
            }

        }
        var user = this.userService.findByUsername(userRegisterBindingModel.getUsername());
//        this.emailService.sendRegistrationMessage(userRegisterBindingModel.getEmail(), user.getId(), userRegisterBindingModel.getUsername());
        return this.view("login");
    }

    @GetMapping("/confirmEmail/{id}")
    public ModelAndView registerConfirm(@PathVariable("id") String id) {
        this.userService.activateUser(this.userService.findById(id));
        return this.redirect("/login");
    }

    @GetMapping("/leaderboard")
    public ModelAndView getLeaderboard(ModelAndView modelAndView) {
        return this.view("leaderboard", modelAndView);
    }

}
