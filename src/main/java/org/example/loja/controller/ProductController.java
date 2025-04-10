package org.example.loja.controller;

import org.example.loja.dto.ProductDTO;
import org.example.loja.services.ProductsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductsServices productsServices;

    @PostMapping
    public ResponseEntity<?> post(@RequestBody ProductDTO product){
        try {
            Long id = productsServices.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product created", "id", id));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }

    }
    @PutMapping(value = "/set-quantity")
    public ResponseEntity<?> updateProductQuantity(@RequestParam long productId, @RequestParam int quantity){
        try{
            int productQuantity = productsServices.updateProductQuantity(productId, quantity);
            return ResponseEntity.ok().body(Map.of("message", "Product quantity updated", "productQuantity", productQuantity, "productId", productId));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        try{
            boolean isDeleted = productsServices.deleteProduct(id);
            if(isDeleted){
                return ResponseEntity.accepted().body(Map.of("message", "Product deleted"));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
