package com.accounting.easy.service;

import com.accounting.easy.domain.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void store(MultipartFile file, User user);

    Stream<Path> loadAll();

    Path load(String filename);

}
