package com.accounting.easy.domain.entities;

import javax.persistence.*;

@Entity
@Table(name="company_employees")
public class CompanyEmployee extends BaseEntity {

    private String username;

    private Company company;

    public CompanyEmployee(){

    }

    @ManyToOne
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }


    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
