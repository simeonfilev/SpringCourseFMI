package com.accounting.easy.web;

import com.accounting.easy.domain.entities.Document;
import com.accounting.easy.domain.entities.User;
import com.accounting.easy.service.DocumentService;
import com.accounting.easy.service.StorageService;
import com.accounting.easy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class DocumentController extends BaseController {

    private final DocumentService documentService;
    private final UserService userService;
    private final StorageService storageService;

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    @Autowired
    public DocumentController(DocumentService documentService, UserService userService, StorageService storageService) {
        this.documentService = documentService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @GetMapping(path = "/api/documents", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Document> getAllDocumentsBasedOnUser(Authentication authentication){
        Optional<User> user = this.userService.findByUsername(authentication.getName());
        return user.map(value -> documentService.getAllDocudemntsByUsername(value.getUsername())).orElse(null);
    }

    @GetMapping(path = "/documents")
    public ModelAndView getDocumentsPage(Authentication authentication,ModelAndView modelAndView) {
        Optional<User> user = this.userService.findByUsername(authentication.getName());

        if(user.isPresent()){
            List<Document> documents = this.documentService.getAllDocudemntsByUser(user.get());
            modelAndView.addObject("documents", documents);
        }else{
            modelAndView.addObject("documents", new ArrayList<>());
        }

        return this.view("documents", modelAndView);
    }

    @GetMapping("/flux")
    public Flux<Document> getAllDocuments(Authentication authentication){
        Optional<User> user = this.userService.findByUsername(authentication.getName());
        return documentService.getAllDocudemntsByUserFlux(user.get());
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable("fileName") String fileName) throws IOException {

        File file = new File(uploadDir + File.separator + fileName );

        HttpHeaders header = new HttpHeaders();
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping("/upload")
    public ModelAndView uploadDocument(Authentication authentication, @RequestParam("documents") MultipartFile[] files){
        Optional<User> user = this.userService.findByUsername(authentication.getName());

        user.ifPresent(value -> Arrays.asList(files).forEach(x -> storageService.store(x, value)));

        return redirect("/documents");
    }

    @PostMapping("/deleteDocument/{documentId}")
    public ModelAndView removeDocumentById(Authentication authentication, @PathVariable("documentId") String docId){
        Optional<User> user = this.userService.findByUsername(authentication.getName());

        user.ifPresent(x -> {this.documentService.deleteDocumentById(docId,user.get());});
        return redirect("/documents");
    }
}
