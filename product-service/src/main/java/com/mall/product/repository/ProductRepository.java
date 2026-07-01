package com.mall.product.repository;

import com.mall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatus(Integer status, Pageable pageable);

    Page<Product> findByCategoryIdAndStatus(Long categoryId, Integer status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND " +
           "(p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 1 AND p.category.id = :categoryId AND " +
           "(p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByCategory(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);
}
