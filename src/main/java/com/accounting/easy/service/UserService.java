package com.accounting.easy.service;


import com.accounting.easy.domain.entities.Roles;
import com.accounting.easy.domain.entities.User;
import com.accounting.easy.domain.models.service.CompanyEmployeeServiceModel;
import com.accounting.easy.domain.models.service.CompanyServiceModel;
import com.accounting.easy.domain.models.service.AccountantServiceModel;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

public interface UserService extends UserDetailsService {
    boolean createUser(AccountantServiceModel accountantServiceModel);

    boolean createUser(CompanyServiceModel companyServiceModel);

    boolean createUser(CompanyEmployeeServiceModel companyEmployeeServiceModel);

    Set<AccountantServiceModel> getAll();

    boolean promoteUser(String id);

    boolean demoteUser(String id);

    boolean activateUser(User user);

    boolean alreadyExistByEmail(String email);

    boolean alreadyExistByUsername(String username);

    boolean deleteByUsername(String username, Authentication authentication);

    Optional<User> findByUsername(String username);

    User findById(String id);

    LinkedHashSet<User> getAllUsers();

    LinkedHashSet<User> getAllUsersByRole(Roles role);

    boolean changePassword(User user, String oldPassword, String newPassword);

    void deleteById(String id, Authentication authentication);
}
