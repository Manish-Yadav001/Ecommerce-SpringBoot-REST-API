package com.Project.EcommerceApp.repository;
import com.Project.EcommerceApp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>{


      @Query("SELECT p FROM Product p WHERE p.product_name = :productName")
      Optional<Product> findByProductName(@Param("productName") String productName);
      Optional<Product> findById(int id);

}