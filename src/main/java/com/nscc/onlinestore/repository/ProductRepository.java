package com.nscc.onlinestore.repository;

import com.nscc.onlinestore.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>{

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    @Modifying
    @Transactional
    @Query("""
        UPDATE Product p
        SET p.prodStockCount = p.prodStockCount - :qty
        WHERE p.prodID = :id AND p.prodStockCount >= :qty
        """)
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);
}
