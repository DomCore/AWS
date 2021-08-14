package com.project.repository;

import com.project.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository {

    @Query(value=" select products.name, product_box.amount from products, boxes inner join product_box on id where products.id=product_box.product_id and boxes.id=product_box.box_id and boxes.id=:id; ",nativeQuery = true)
    List<Product> getProductsFromBox(@Param("id") int id);

}
