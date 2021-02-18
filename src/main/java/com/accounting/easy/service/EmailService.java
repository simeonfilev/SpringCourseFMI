package com.accounting.easy.service;

public interface EmailService {
    void sendRegistrationMessage(String to, String id, String username);
}
