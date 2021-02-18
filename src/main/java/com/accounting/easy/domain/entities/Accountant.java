package com.accounting.easy.domain.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="accountants")
public class Accountant  extends BaseEntity{
    private String username;

    private String name;

    private List<Company> companies;

    private String email;

    public Accountant(){

    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Column(name = "full_name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany
    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
