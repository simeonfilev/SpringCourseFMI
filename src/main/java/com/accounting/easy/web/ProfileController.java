package com.accounting.easy.web;

import com.accounting.easy.domain.entities.User;
import com.accounting.easy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProfileController extends BaseController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/myProfile")
    public ModelAndView myProfile(Authentication authentication, ModelAndView modelAndView) {
        return this.redirect("/search/?username="+authentication.getName());
    }

    @GetMapping("/myProfile/settings")
    public ModelAndView myProfileSettings(ModelAndView modelAndView, Authentication authentication) {
        return view("settings", modelAndView);
    }

    @PostMapping(value = "/changePassword")
    public ModelAndView changePassword(ModelAndView modelAndView, Authentication authentication,
                                       @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword,
                                       @RequestParam("newPasswordConfirm") String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            modelAndView.addObject("passwordNotMatch", true);
            return this.view("settings", modelAndView);
        }
        boolean changed = this.userService.changePassword(this.userService.findByUsername(authentication.getName()).get(), oldPassword, newPassword);
        if (!changed) {
            modelAndView.addObject("changed", true);
            return this.view("settings", modelAndView);
        }
        return redirect("/");
    }

    @GetMapping("/profile/{name}")
    public ModelAndView profile(ModelAndView modelAndView, @PathVariable("name") String name) {

        modelAndView.addObject("username", name);
        return this.view("profile", modelAndView);
    }

    @GetMapping("/search/")
    public ModelAndView searchProfile(ModelAndView modelAndView,@RequestParam("username") String name) {
        Optional<User> user = this.userService.findByUsername(name);

        if(user.isPresent()){
            modelAndView.addObject("username",user.get().getUsername());
            modelAndView.addObject("email",user.get().getEmail());
            modelAndView.addObject("role",user.get().getRole().toString().replace("ROLE_","").toLowerCase());
        }
        return this.view("profile", modelAndView);
    }
}
