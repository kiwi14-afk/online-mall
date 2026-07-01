package com.mall.product.service;

import com.mall.product.dto.ProductRequest;
import com.mall.product.dto.ProductResponse;
import com.mall.product.entity.Category;
import com.mall.product.entity.Product;
import com.mall.product.repository.CategoryRepository;
import com.mall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> listProducts(String keyword, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            productPage = productRepository.searchByCategory(categoryId, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            productPage = productRepository.search(keyword, pageable);
        } else if (categoryId != null) {
            productPage = productRepository.findByCategoryIdAndStatus(categoryId, 1, pageable);
        } else {
            productPage = productRepository.findByStatus(1, pageable);
        }

        return productPage.map(ProductResponse::fromEntity);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return ProductResponse.fromEntity(product);
    }

    /**
     * 批量获取商品信息（供 order-service Feign 调用）
     */
    public List<ProductResponse> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .status(1)
                .build();

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("分类不存在"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setStatus(0);  // 软删除
        productRepository.save(product);
    }

    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStock() < quantity) {
            return false;
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
        return true;
    }

    @Transactional
    public void rollbackStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    // --- Category management ---

    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("分类已存在");
        }
        Category category = Category.builder().name(name).build();
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
