package com.accounting.easy.domain.entities;

import javax.persistence.*;

@Entity
@Table(name="documents")
public class Document extends BaseEntity{

    private Company company;

    private String fileName;

    private String userId;

    public Document(){ }

    @Column(name = "file_name", nullable = false)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @ManyToOne
    @JoinColumn(name = "company_id")
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Column(name = "user", nullable = false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
