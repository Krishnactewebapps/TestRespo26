package com.example.productservice.service;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product("Test Product", "Test Description", new BigDecimal("10.00"), 5);
        product.setId(1L);
    }

    @Test
    void getAllProducts_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products);
        List<Product> result = productService.getAllProducts();
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    void getProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> result = productService.getProductById(1L);
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.getProductById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void createProduct_ValidProduct_Success() {
        when(validator.validate(any(Product.class))).thenReturn(Collections.emptySet());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product created = productService.createProduct(product);
        assertEquals(product, created);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void createProduct_InvalidProduct_ThrowsException() {
        Set<ConstraintViolation<Product>> violations = new HashSet<>();
        ConstraintViolation<Product> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Invalid");
        violations.add(violation);
        when(validator.validate(any(Product.class))).thenReturn(violations);
        assertThrows(ConstraintViolationException.class, () -> productService.createProduct(product));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_ValidProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(validator.validate(any(Product.class))).thenReturn(Collections.emptySet());
        when(productRepository.save(any(Product.class))).thenReturn(product);
        Product details = new Product("Updated", "Desc", new BigDecimal("20.00"), 10);
        Product updated = productService.updateProduct(1L, details);
        assertEquals("Updated", updated.getName());
        assertEquals("Desc", updated.getDescription());
        assertEquals(new BigDecimal("20.00"), updated.getPrice());
        assertEquals(10, updated.getStock());
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        Product details = new Product("Updated", "Desc", new BigDecimal("20.00"), 10);
        when(validator.validate(any(Product.class))).thenReturn(Collections.emptySet());
        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(2L, details));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);
        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.existsById(2L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(2L));
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchProductsByName_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(products);
        List<Product> result = productService.searchProductsByName("Test");
        assertEquals(1, result.size());
    }

    @Test
    void findProductsByPriceGreaterThanEqual_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByPriceGreaterThanEqual(new BigDecimal("10.00"))).thenReturn(products);
        List<Product> result = productService.findProductsByPriceGreaterThanEqual(new BigDecimal("10.00"));
        assertEquals(1, result.size());
    }

    @Test
    void findProductsByStockLessThan_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByStockLessThan(10)).thenReturn(products);
        List<Product> result = productService.findProductsByStockLessThan(10);
        assertEquals(1, result.size());
    }

    @Test
    void findProductsByPriceBetween_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByPriceBetween(new BigDecimal("5.00"), new BigDecimal("15.00"))).thenReturn(products);
        List<Product> result = productService.findProductsByPriceBetween(new BigDecimal("5.00"), new BigDecimal("15.00"));
        assertEquals(1, result.size());
    }

    @Test
    void findProductsByNameAndStockGreaterThan_ReturnsList() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findByNameAndStockGreaterThan("Test", 1)).thenReturn(products);
        List<Product> result = productService.findProductsByNameAndStockGreaterThan("Test", 1);
        assertEquals(1, result.size());
    }
}
