package org.example.loja.controller;

import org.example.loja.dto.ProductDTO;
import org.example.loja.services.ProductsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product Management", description = "Operations related to product management")
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    @Autowired
    private ProductsServices productsServices;

    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product data provided",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping
    public ResponseEntity<?> post(@RequestBody ProductDTO product) {
        try {
            Long id = productsServices.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product created", "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }


    @Operation(summary = "Update product quantity", description = "Updates the inventory quantity of a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product quantity updated successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product ID or quantity provided",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping(value = "/set-quantity")
    public ResponseEntity<?> updateProductQuantity(
            @Parameter(description = "ID of the product to update", required = true, example = "123")
            @RequestParam long productId,

            @Parameter(description = "New quantity value", required = true, example = "50")
            @RequestParam int quantity) {
        try {
            int productQuantity = productsServices.updateProductQuantity(productId, quantity);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Product quantity updated",
                    "productQuantity", productQuantity,
                    "productId", productId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }


    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Product deleted successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID of the product to delete", required = true, example = "123")
            @PathVariable long id) {
        try {
            boolean isDeleted = productsServices.deleteProduct(id);
            if (isDeleted) {
                return ResponseEntity.accepted().body(Map.of("message", "Product deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}