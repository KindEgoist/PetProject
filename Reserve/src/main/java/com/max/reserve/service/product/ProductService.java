package com.max.reserve.service.product;

import com.max.reserve.model.Product;

public interface ProductService {
    Product getProduct(Long id);
    Product saveProduct(Product product);

}
