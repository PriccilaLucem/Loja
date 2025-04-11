package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.ProductEntity;
import org.example.loja.services.ProductsServices;
import org.example.loja.services.StoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product Management", description = "Operations related to product management")
@RestController
@RequestMapping("/api/v1/store/{storeId}/product")
public class ProductController {

    @Autowired
    private ProductsServices productsServices;

    @Autowired
    private StoreService storeService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"New Product\", \"price\": 19.99, \"quantity\": 100}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Product created\", \"id\": 123}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Product name cannot be empty\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"An unexpected error occurred\"}"
                            )
                    )
            )
    })
    @PostMapping()
    public ResponseEntity<?> post(@RequestBody ProductDTO product, @PathVariable Long id) {
        try {
            logger.info("Attempting to create product for storeId={} with data={}", id, product);
            ProductEntity createdProduct = productsServices.createProduct(product);

            boolean linkedWithStore = storeService.addProductsToStore(id, createdProduct);
            if (linkedWithStore) {
                logger.info("Product created successfully with id={} and linked to storeId={}", createdProduct.getId(), id);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product created", "id", createdProduct.getId()));
            }

            productsServices.deleteProduct(createdProduct.getId());
            logger.error("Failed to link product id={} to storeId={}, product deleted", createdProduct.getId(), id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Product creation failed"));
        } catch (IllegalArgumentException e) {
            logger.warn("Product creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while creating product for storeId={}", id, e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(
            summary = "Update product quantity",
            description = "Updates the inventory quantity of a specific product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product quantity updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Product quantity updated\", \"productQuantity\": 50, \"productId\": 123}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product ID or quantity provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Quantity cannot be negative\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"An unexpected error occurred\"}"
                            )
                    )
            )
    })
    public ResponseEntity<?> updateProductQuantity(
            @RequestParam long productId,
            @RequestParam int quantity) {
        try {
            logger.info("Updating quantity for productId={} to {}", productId, quantity);
            int productQuantity = productsServices.updateProductQuantity(productId, quantity);

            logger.info("Quantity update successful for productId={}, newQuantity={}", productId, productQuantity);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Product quantity updated",
                    "productQuantity", productQuantity,
                    "productId", productId));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update quantity for productId={}: {}", productId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while updating quantity for productId={}", productId, e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Product deleted\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Product not found\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"An unexpected error occurred\"}"
                            )
                    )
            )
    })
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            logger.info("Attempting to delete productId={}", id);
            boolean isDeleted = productsServices.deleteProduct(id);

            if (isDeleted) {
                logger.info("Product deleted successfully, productId={}", id);
                return ResponseEntity.accepted().body(Map.of("message", "Product deleted"));
            } else {
                logger.warn("Product not found for deletion, productId={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error while deleting productId={}", id, e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Operation(
            summary = "Update product details",
            description = "Updates all details of an existing product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"message\": \"Product updated successfully\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Invalid product data\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Product not found\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\"error\": \"An unexpected error occurred\"}"
                            )
                    )
            )
    })
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable long productId, @RequestBody ProductEntity product) {
        try {
            product.setId(productId);
            logger.info("Attempting to update product with productId={} and data={}", productId, product);

            boolean isUpdated = productsServices.updateProduct(product);
            if (isUpdated) {
                logger.info("Product updated successfully, productId={}", productId);
                return ResponseEntity.ok().body(Map.of("message", "Product updated successfully"));
            } else {
                logger.warn("Product not found for update, productId={}", productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
            }
        } catch (Exception e) {
            logger.error("Unexpected error while updating productId={}", productId, e);
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}