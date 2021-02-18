package com.accounting.easy.interceptors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        try {
            List<String> roles = SecurityContextHolder.getContext()
                    .getAuthentication().getAuthorities().stream().map(x -> x.getAuthority().toString()).collect(Collectors.toUnmodifiableList());

            if(roles.contains("ROLE_ADMIN")){
                request.setAttribute("authorization","ROLE_ADMIN");
            }else{
                request.setAttribute("authorization", SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().get().toString());
            }
        } catch (Exception e) {
        }
    }
}
