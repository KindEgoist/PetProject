package com.max.productInfo.service;

import com.max.productInfo.dto.ProductInfo;
import org.springframework.stereotype.Service;

@Service
public interface ProductInfoService {
    ProductInfo getProductById(Long productId);

}
