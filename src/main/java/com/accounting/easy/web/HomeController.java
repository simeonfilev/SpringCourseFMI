package com.accounting.easy.web;


import com.accounting.easy.domain.entities.Roles;
import com.accounting.easy.service.DocumentService;
import com.accounting.easy.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController extends BaseController {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final DocumentService documentService;

    @Autowired
    public HomeController(ModelMapper modelMapper, UserService userService, DocumentService documentService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.documentService = documentService;
    }


    @GetMapping("/")
    public ModelAndView getHomePage(ModelAndView modelAndView) {
        modelAndView.addObject("usersSize", this.userService.getAllUsers().size());
        modelAndView.addObject("accountantsSize", this.userService.getAllUsersByRole(Roles.ROLE_ACCOUNTANT).size());
        modelAndView.addObject("companiesSize", this.userService.getAllUsersByRole(Roles.ROLE_COMPANY).size());
        modelAndView.addObject("documentsSize", this.documentService.getAllDocumentsSize());
        return this.view("index", modelAndView);
    }

    @GetMapping("/help")
    public ModelAndView getHelpPage(ModelAndView modelAndView) {
        return this.view("help", modelAndView);
    }

    @GetMapping("/upload")
    public ModelAndView getUploadDocumentPage(ModelAndView modelAndView){
        return this.view("upload", modelAndView);
    }
}
