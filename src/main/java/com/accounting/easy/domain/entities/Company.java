package com.accounting.easy.domain.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="companies")
public class Company  extends BaseEntity {
    private String username;

    private String companyName;

    private String address;

    private String EIK;

    private List<CompanyEmployee> employees;

    private List<Document> documents;

    private Accountant accountant;

    public Company(){

    }
    @Column(name = "company_name", nullable = false, unique = true)
    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name = "eik", nullable = false, unique = true)
    public String getEIK() {
        return this.EIK;
    }

    public void setEIK(String eik) {
        this.EIK = eik;
    }

    @Column(name = "address", nullable = false)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<CompanyEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<CompanyEmployee> employees) {
        this.employees = employees;
    }


    @ManyToOne(cascade = CascadeType.ALL)
    public Accountant getAccountant() {
        return accountant;
    }

    public void setAccountant(Accountant accountant) {
        this.accountant = accountant;
    }


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }


    @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
