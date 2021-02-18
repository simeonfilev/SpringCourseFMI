package com.accounting.easy.config;

import com.accounting.easy.domain.entities.UserRole;
import com.accounting.easy.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup){
            return;
        }

        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_COMPANY");
        createRoleIfNotFound("ROLE_COMPANY_USER");
        createRoleIfNotFound("ROLE_ACCOUNTANT");
        createRoleIfNotFound("ROLE_GUEST");

        alreadySetup = true;
    }

    @Transactional
    UserRole createRoleIfNotFound(String name) {

        UserRole role = roleRepository.getByAuthority(name);
        if (role == null) {
            role = new UserRole(name);
            roleRepository.save(role);
        }
        return role;
    }
}