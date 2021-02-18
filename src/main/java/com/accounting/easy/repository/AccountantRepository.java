package com.accounting.easy.repository;

import com.accounting.easy.domain.entities.Accountant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountantRepository extends JpaRepository<Accountant, String> {
    Optional<Accountant> findByUsername(String username);
}
