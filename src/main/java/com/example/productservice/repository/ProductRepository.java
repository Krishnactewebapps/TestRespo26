package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products with price greater than or equal to a value
    List<Product> findByPriceGreaterThanEqual(BigDecimal price);

    // Find products with stock less than a value
    List<Product> findByStockLessThan(Integer stock);

    // Custom query: Find products by price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Custom query: Find products by name and stock greater than a value
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.stock > :stock")
    List<Product> findByNameAndStockGreaterThan(@Param("name") String name, @Param("stock") Integer stock);
}
