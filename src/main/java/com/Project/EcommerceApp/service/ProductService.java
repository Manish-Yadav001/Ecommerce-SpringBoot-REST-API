package com.Project.EcommerceApp.service;

import com.Project.EcommerceApp.entity.Product;
import com.Project.EcommerceApp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public String addProduct(Product product ){
        Optional<Product> existProduct= productRepository.findByProductName(product.getProduct_name());
        if(existProduct.isPresent()){
            return "PRODUCT ALREADY EXIST";
        }
        else {
            productRepository.save(product);
            return "PRODUCT SAVED SUCCESSFULLY";
        }
    }

    public String deleteProduct(int id){
        Optional<Product> existProduct=productRepository.findById(id);
        if(existProduct.isEmpty()){
            return "PRODUCT DOESN'T EXIST";
        }
        else{
            productRepository.delete(existProduct.get());
            return "PRODUCT DELETED SUCCESSFULLY";
        }
    }

    public String updateProducts(String name,Product updatedProduct){
        Optional<Product> existsProduct=productRepository.findByProductName(name);

        if(existsProduct.isEmpty()){
            return "PRODUCT DOESN'T EXIST";
        }
        Product existproduct=existsProduct.get();
        existproduct.setProduct_name(updatedProduct.getProduct_name());
        existproduct.setStock_quantity(updatedProduct.getStock_quantity());
        existproduct.setPrice(updatedProduct.getPrice());
        productRepository.save(existproduct);
        return "INVENTORY UPDATED SUCCESSFULLY";
    }


}
