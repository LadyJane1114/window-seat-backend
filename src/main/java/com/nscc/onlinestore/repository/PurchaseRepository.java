package com.nscc.onlinestore.repository;

import com.nscc.onlinestore.entity.Purchase;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @EntityGraph(attributePaths = {"lineItems"})
    Optional<Purchase> findById(Long id);
}
