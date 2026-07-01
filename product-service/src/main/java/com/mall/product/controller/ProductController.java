package com.mall.product.controller;

import com.mall.product.common.ApiResult;
import com.mall.product.dto.ProductRequest;
import com.mall.product.dto.ProductResponse;
import com.mall.product.dto.StockRequest;
import com.mall.product.entity.Category;
import com.mall.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // ==================== Product APIs ====================

    @GetMapping("/list")
    public ApiResult<Page<ProductResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<ProductResponse> result = productService.listProducts(keyword, categoryId, page, size);
        return ApiResult.success(result);
    }

    @GetMapping("/{id}")
    public ApiResult<ProductResponse> getProduct(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProduct(id);
            return ApiResult.success(product);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    /**
     * 批量获取商品信息（供 order-service Feign 调用）
     */
    @GetMapping("/batch")
    public ApiResult<List<ProductResponse>> getProductsByIds(@RequestParam List<Long> ids) {
        List<ProductResponse> products = productService.getProductsByIds(ids);
        return ApiResult.success(products);
    }

    @PostMapping
    public ApiResult<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse product = productService.createProduct(request);
            return ApiResult.success("创建成功", product);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResult<ProductResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse product = productService.updateProduct(id, request);
            return ApiResult.success("更新成功", product);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ApiResult.success("下架成功", null);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/stock/deduct")
    public ApiResult<Boolean> deductStock(@PathVariable Long id,
                                           @Valid @RequestBody StockRequest request) {
        boolean success = productService.deductStock(id, request.getQuantity());
        if (success) {
            return ApiResult.success(true);
        }
        return ApiResult.error("库存不足");
    }

    @PutMapping("/{id}/stock/rollback")
    public ApiResult<Void> rollbackStock(@PathVariable Long id,
                                          @Valid @RequestBody StockRequest request) {
        productService.rollbackStock(id, request.getQuantity());
        return ApiResult.success("库存回滚成功", null);
    }

    // ==================== Category APIs ====================

    @GetMapping("/category/list")
    public ApiResult<List<Category>> listCategories() {
        List<Category> categories = productService.listCategories();
        return ApiResult.success(categories);
    }

    @PostMapping("/category")
    public ApiResult<Category> createCategory(@RequestParam String name) {
        try {
            Category category = productService.createCategory(name);
            return ApiResult.success("创建成功", category);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/category/{id}")
    public ApiResult<Void> deleteCategory(@PathVariable Long id) {
        try {
            productService.deleteCategory(id);
            return ApiResult.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }
}
