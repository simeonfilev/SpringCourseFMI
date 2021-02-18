package com.accounting.easy.service;

import com.accounting.easy.domain.entities.*;
import com.accounting.easy.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    private final DocumentRepository documentRepository;

    private final CompanyService companyService;

    private final CompanyEmployeeService companyEmployeeService;

    @Autowired
    public StorageServiceImpl(DocumentRepository documentRepository, CompanyService companyService, CompanyEmployeeService companyEmployeeService) {
        this.documentRepository = documentRepository;
        this.companyService = companyService;
        this.companyEmployeeService = companyEmployeeService;
    }

    @Override
    public void store(MultipartFile file, User user) {
        Document document = new Document();
        if(user.getRole().equals(Roles.ROLE_COMPANY)){
            Optional<Company> company = this.companyService.findCompanyByUsername(user.getUsername());

            if(!company.isPresent()){
                return;
            }
            String fileName = org.apache.commons.io.FilenameUtils.getName(file.getOriginalFilename());

            document.setCompany(company.get());
            document.setFileName(fileName);
            document.setUserId(company.get().getId());

            try{
                Path copyLocation = Paths
                        .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
                Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                documentRepository.saveAndFlush(document);
            }catch (Exception e){

            }
        }else if(user.getRole().equals(Roles.ROLE_COMPANY_USER)){

            Optional<CompanyEmployee> companyEmployee = this.companyEmployeeService.getByUsername(user.getUsername());

            if(!companyEmployee.isPresent()){
                return;
            }
            String fileName = org.apache.commons.io.FilenameUtils.getName(file.getOriginalFilename());

            document.setCompany(companyEmployee.get().getCompany());
            document.setFileName(fileName);
            document.setUserId(user.getId());

            try{
                Path copyLocation = Paths
                        .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
                Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                documentRepository.saveAndFlush(document);
            }catch (Exception e){

            }

        }else if(user.getRole().equals(Roles.ROLE_ACCOUNTANT)){
            // cant add documents
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename) {
        return null;
    }
}
