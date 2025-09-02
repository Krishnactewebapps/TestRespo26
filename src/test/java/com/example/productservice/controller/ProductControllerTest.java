package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Test Description", new BigDecimal("10.00"), 5);
        product.setId(1L);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getAllProducts_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(products);
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(product.getName())));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getProductById_Found() throws Exception {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(product.getName())));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void getProductById_NotFound() throws Exception {
        when(productService.getProductById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createProduct_Valid_ReturnsCreated() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(product.getName())));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createProduct_Invalid_ReturnsBadRequest() throws Exception {
        Product invalidProduct = new Product("", "", null, null);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateProduct_Valid_ReturnsOk() throws Exception {
        Product updated = new Product("Updated", "Desc", new BigDecimal("20.00"), 10);
        updated.setId(1L);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updated);
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateProduct_Invalid_ReturnsBadRequest() throws Exception {
        Product invalidProduct = new Product("", "", null, null);
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteProduct_NotFound_ReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Product not found with id: 2")).when(productService).deleteProduct(2L);
        mockMvc.perform(delete("/api/products/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Product not found")));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void searchProductsByName_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProductsByName("Test")).thenReturn(products);
        mockMvc.perform(get("/api/products/search").param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(product.getName())));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void findProductsByPriceGreaterThanEqual_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findProductsByPriceGreaterThanEqual(new BigDecimal("10.00"))).thenReturn(products);
        mockMvc.perform(get("/api/products/price/min").param("price", "10.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(10.00)));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void findProductsByStockLessThan_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findProductsByStockLessThan(10)).thenReturn(products);
        mockMvc.perform(get("/api/products/stock/max").param("stock", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].stock", is(5)));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void findProductsByPriceBetween_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findProductsByPriceBetween(new BigDecimal("5.00"), new BigDecimal("15.00"))).thenReturn(products);
        mockMvc.perform(get("/api/products/price/range")
                        .param("minPrice", "5.00")
                        .param("maxPrice", "15.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price", is(10.00)));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void findProductsByNameAndStockGreaterThan_ReturnsList() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findProductsByNameAndStockGreaterThan("Test", 1)).thenReturn(products);
        mockMvc.perform(get("/api/products/search/stock")
                        .param("name", "Test")
                        .param("stock", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(product.getName())));
    }

    @Test
    void getAllProducts_ForbiddenForAnonymous() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    void createProduct_ForbiddenForUser() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden());
    }
}
