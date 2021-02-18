package com.accounting.easy.repository;

import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,String> {
    List<Document> findAllByCompany(Company company);

    List<Document> findAllByUserId(String userId);


}
