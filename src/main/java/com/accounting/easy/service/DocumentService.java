package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Company;
import com.accounting.easy.domain.entities.Document;
import com.accounting.easy.domain.entities.User;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DocumentService {

    List<Document> getAllDocumentsBasedOnCompany(Company company);

    Long getAllDocumentsSize();

    List<Document> getAllDocudemntsByUsername(String user);

    List<Document> getAllDocudemntsByUser(User user);

    Flux<Document> getAllDocudemntsByUserFlux(User user);

    List<Document> getAllDocuments();

    void deleteDocumentById(String id, User user);

    Flux<Document> findAll();

}
