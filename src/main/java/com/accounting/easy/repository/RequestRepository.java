package com.accounting.easy.repository;

import com.accounting.easy.domain.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request,String> {
    List<Request> findAllByToUsername(String username);

}
