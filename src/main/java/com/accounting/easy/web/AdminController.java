package com.accounting.easy.web;


import com.accounting.easy.domain.entities.Document;
import com.accounting.easy.domain.entities.User;
import com.accounting.easy.service.DocumentService;
import com.accounting.easy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashSet;
import java.util.List;

@Controller
public class AdminController extends BaseController {

    private final UserService userService;
    private final DocumentService documentService;

    @Autowired
    public AdminController(UserService userService, DocumentService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }


//    @PostMapping(value = "/delete/{userId}")
//    public ModelAndView deleteUser(@PathVariable("userId") String user, Authentication authentication) {
//        this.userService.deleteByUsername(this.userService.findByUsername(user).get().getUsername(), authentication);
//        return redirect("/admin");
//    }
//

    @GetMapping(path = "/admin/documents")
    public ModelAndView getAdminDocuments(ModelAndView modelAndView){
        List<Document> documents = this.documentService.getAllDocuments();
        modelAndView.addObject("documents", documents);
        return this.view("documents", modelAndView);
    }

    @GetMapping(path = "/admin/users")
    public ModelAndView getAdminUsers(ModelAndView modelAndView){
        LinkedHashSet<User> users = this.userService.getAllUsers();
        modelAndView.addObject("users", users);
        return this.view("admin-users", modelAndView);
    }

    @PostMapping(path = "/admin/deleteAccount/{userId}")
    public ModelAndView deleteAccount(@PathVariable("userId") String userForDelete, Authentication authentication){
        this.userService.deleteById(userForDelete,authentication);
        return this.redirect("/admin/users");
    }
}
