package com.Project.EcommerceApp.controller;

import com.Project.EcommerceApp.entity.Product;
import com.Project.EcommerceApp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/addproduct")
    public ResponseEntity<String> addProducts(@RequestBody Product product){
        String status=productService.addProduct(product);
        if(status.equals("PRODUCT ALREADY EXIST")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PRODUCT ALREADY EXIST");
        }
        else {
            return ResponseEntity.status(HttpStatus.CREATED).body("PRODUCT ADDED SUCCESSFULLY");
        }
    }

    @DeleteMapping("/deleteProduct/{id}")
   public ResponseEntity<String> deleteProduct(@PathVariable int id){
        String status= productService.deleteProduct(id);
        if(status.equals("PRODUCT DOESN'T EXIST")){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PRODUCT DOESN'T EXIST");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body("PRODUCT DELETED SUCCESSFULLY");
        }
   }

   @PutMapping("/updateinventory/{name}")
    public ResponseEntity<String> updateProduct(@RequestBody Product product,@PathVariable String name){
       String status= productService.updateProducts(name,product);

       if(status.equals("PRODUCT DOESN'T EXIST")){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PRODUCT DOESN'T EXIST");
       }
       else {
           return ResponseEntity.status(HttpStatus.OK).body("INVENTORY UPDATED SUCCESSFULLY");
       }
   }
}
