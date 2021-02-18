package com.accounting.easy.service;

import com.accounting.easy.domain.entities.*;
import com.accounting.easy.domain.models.service.AccountantServiceModel;
import com.accounting.easy.domain.models.service.CompanyEmployeeServiceModel;
import com.accounting.easy.domain.models.service.CompanyServiceModel;
import com.accounting.easy.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final CompanyService companyService;

    private final CompanyEmployeeRepository companyEmployeeRepository;

    private final AccountantRepository accountantRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, CompanyRepository companyRepository, CompanyService companyService, CompanyEmployeeRepository companyEmployeeRepository, AccountantRepository accountantRepository, RoleRepository roleRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.companyEmployeeRepository = companyEmployeeRepository;
        this.accountantRepository = accountantRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private Set<UserRole> getAuthorities(String authority) {
        Set<UserRole> userAuthorities = new HashSet<>();

        userAuthorities.add(this.roleRepository.getByAuthority(authority));

        return userAuthorities;
    }


    private String getUserAuthority(String userId) {
        if (!this.userRepository.findById(userId).isPresent()) {
            return null;
        }
        if (!this.userRepository.findById(userId).get().getAuthorities().stream().findFirst().isPresent()) {
            return null;
        }
        return this.userRepository.findById(userId).get().getAuthorities().stream().findFirst().get().getAuthority();
    }

    @Override
    public boolean createUser(AccountantServiceModel accountantServiceModel) {
        Accountant accountant = this.modelMapper.map(accountantServiceModel, Accountant.class);

        User user = this.modelMapper.map(accountantServiceModel,User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(accountantServiceModel.getPassword()));
        user.setRole(Roles.ROLE_ACCOUNTANT);
        user.setAuthorities(Collections.singleton(this.roleRepository.getByAuthority(Roles.ROLE_ACCOUNTANT.toString())));
        user.setEnabled(true);

        accountant.setUsername(user.getUsername());

        try {
            this.userRepository.save(user);
            this.accountantRepository.save(accountant);
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    @Override
    public boolean createUser(CompanyServiceModel companyServiceModel) {
        User user = this.modelMapper.map(companyServiceModel,User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(companyServiceModel.getPassword()));
        user.setRole(Roles.ROLE_COMPANY);
        user.setAuthorities(Collections.singleton(this.roleRepository.getByAuthority(Roles.ROLE_COMPANY.toString())));
        user.setEnabled(true);

        Company company = this.modelMapper.map(companyServiceModel, Company.class);
        company.setEmployees(new ArrayList<>());
        company.setDocuments(new ArrayList<>());
        company.setUsername(user.getUsername());

        try {
            this.userRepository.save(user);
            this.companyRepository.save(company);
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    @Override
    public boolean createUser(CompanyEmployeeServiceModel companyEmployeeServiceModel) {
        CompanyEmployee employee = this.modelMapper.map(companyEmployeeServiceModel, CompanyEmployee.class);

        try{
            Optional<Company> company = companyRepository.findCompanyByUsername(companyEmployeeServiceModel.getCompanyName());
            if(company.isPresent()){
                employee.setCompany(company.get());
            }else{
                return false;
            }
        }catch (Exception ignore){  return false;}

        User user = this.modelMapper.map(companyEmployeeServiceModel,User.class);
        user.setPassword(this.bCryptPasswordEncoder.encode(companyEmployeeServiceModel.getPassword()));
        user.setRole(Roles.ROLE_COMPANY_USER);
        user.setAuthorities(Collections.singleton(this.roleRepository.getByAuthority(Roles.ROLE_COMPANY_USER.toString())));
        user.setEnabled(true);

        employee.setUsername(user.getUsername());

        try {
            this.userRepository.save(user);
            this.companyEmployeeRepository.save(employee);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    @Override
    public Set<AccountantServiceModel> getAll() {
        return this.userRepository
                .findAll()
                .stream()
                .map(x -> this.modelMapper.map(x, AccountantServiceModel.class))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean promoteUser(String id) {
        User user = this.userRepository
                .findByUsername(id)
                .orElse(null);

        if (user == null) return false;

        String userAuthority = this.getUserAuthority(user.getId());

        if (userAuthority == null) {
            user.setAuthorities(this.getAuthorities("USER"));
            this.userRepository.save(user);
            return true;
        }

        switch (userAuthority) {
            case "USER":
                user.setAuthorities(this.getAuthorities("MODERATOR"));
                break;
            case "MODERATOR":
                user.setAuthorities(this.getAuthorities("ADMIN"));
                break;
            default:
                throw new IllegalArgumentException("There is no role, higher than ADMIN");
        }

        this.userRepository.save(user);
        return true;

    }

    @Override
    public boolean demoteUser(String id) {
        User user = this.userRepository
                .findByUsername(id)
                .orElse(null);

        if (user == null) return false;

        String userAuthority = this.getUserAuthority(user.getId());
        if (userAuthority == null) {
            user.setAuthorities(this.getAuthorities("USER"));
            this.userRepository.save(user);
            return true;
        }


        switch (userAuthority) {
            case "ADMIN":
                user.setAuthorities(this.getAuthorities("MODERATOR"));
                break;
            case "MODERATOR":
                user.setAuthorities(this.getAuthorities("USER"));
                break;
            default:
                throw new IllegalArgumentException("There is no role, lower than USER");
        }

        this.userRepository.save(user);
        return true;
    }

    @Override
    public boolean activateUser(User user) {
        if (user != null && !user.isEnabled()) {
            user.setEnabled(true);
            this.userRepository.saveAndFlush(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean alreadyExistByEmail(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean alreadyExistByUsername(String username) {
        return this.userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public boolean deleteByUsername(String username, Authentication authentication) {
        User deleting = this.userRepository.findByUsername(authentication.getName()).get();
        User toBeDeleted = this.userRepository.findByUsername(username).get();
        try {
            if (this.getUserAuthority(deleting.getId()).equals("ADMIN") && !this.getUserAuthority(toBeDeleted.getId()).equals("ADMIN")) {
                this.userRepository.deleteUserInRoles(toBeDeleted.getId());
                this.userRepository.deleteUserInUsers(toBeDeleted.getId());
                this.userRepository.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<User> findByUsername(String username) {
       return this.userRepository.findByUsername(username);
    }

    @Override
    public User findById(String id) {
        if (this.userRepository.findById(id).isPresent()) {
            return this.userRepository.findById(id).get();
        }
        return null;

    }

    @Override
    public LinkedHashSet<User> getAllUsers() {
        return this.userRepository.getAllUsers();
    }

    @Override
    public LinkedHashSet<User> getAllUsersByRole(Roles role) {
        return this.userRepository.findAllByRole(role);
    }

    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String existingPassword = oldPassword;
        String dbPassword = user.getPassword();

        if (passwordEncoder.matches(existingPassword, dbPassword)) {
            if (newPassword.length() >= 6) {
                user.setPassword(passwordEncoder.encode(newPassword));
                this.userRepository.saveAndFlush(user);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteById(String id, Authentication authentication) {
        Optional<User> user = this.userRepository.findById(id);
        if(user.isPresent()){
            if(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
             && user.get().getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){

                if(user.get().getRole().equals(Roles.ROLE_COMPANY)){
                    this.companyService.deleteCompany(this.companyService.findCompanyByUsername(user.get().getUsername()).get());
                }

                if(user.get().getRole().equals(Roles.ROLE_COMPANY_USER)){
                    this.companyService.deleteUserEmployeeWithId( this.companyEmployeeRepository.findByUsername(user.get().getUsername()).get().getCompany().getUsername()
                            ,this.companyEmployeeRepository.findByUsername(user.get().getUsername()).get().getId());
                }

                if(user.get().getRole().equals(Roles.ROLE_ACCOUNTANT)){
                    this.accountantRepository.delete(this.accountantRepository.findByUsername(user.get().getUsername()).get());
                }

                this.userRepository.deleteById(id);
                this.userRepository.flush();
            }
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) throw new UsernameNotFoundException("No such user.");
        return user;
    }
}
