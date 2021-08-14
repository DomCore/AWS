package com.project.servImpls;

import com.project.model.Product;
import com.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getProductsFromBox(int id) {
        return productRepository.getProductsFromBox(id);
    }
}
