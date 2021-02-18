package com.accounting.easy.service;

import com.accounting.easy.domain.entities.Request;

import java.util.List;

public interface RequestService {
    List<Request> getAllRequestsForToUsername(String username);

    void createRequest(String fromUsername, String toUsername);

    void acceptRequest(String id);

    void deleteRequest(String id,String username);
}
