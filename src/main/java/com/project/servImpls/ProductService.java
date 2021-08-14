package com.project.servImpls;

import com.project.model.Product;
import com.project.repository.ProductRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface ProductService extends ProductRepository {
    List<Product> getProductsFromBox(int id);
}