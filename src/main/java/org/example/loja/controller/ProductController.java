package org.example.loja.controller;

import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.dto.ProductDTO;
import org.example.loja.entities.ProductEntity;
import org.example.loja.enums.TokenType;
import org.example.loja.services.ProductsServices;
import org.example.loja.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private JwtTokenProvider provider;

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
    public ResponseEntity<?> post(@RequestBody ProductDTO product, @RequestHeader(value = "Authorization") String token, @PathVariable Long id) {
        try {
            TokenType type = provider.parseTypeOfToken(token);
            List<String> storeIds = provider.parseStoresFromToken(token, type.name());

            if(type.equals("storeAdmin") && !storeIds.contains(id.toString())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error:","Unauthorized" ));
            }
            if(type.equals("manager") && !storeIds.contains(id.toString())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error:","Unauthorized" ));
            }

            ProductEntity createdProduct = productsServices.createProduct(product);
            storeService.addProductsToStore(id,createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Product created", "id", createdProduct.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID of the product to update", required = true, example = "123")
            @PathVariable long productId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntity.class),
                            examples = @ExampleObject(
                                    value = "{\"name\": \"Updated Product\", \"price\": 29.99, \"quantity\": 150}"
                            )
                    )
            )
            @RequestBody ProductEntity product) {
        try {
            product.setId(productId);
            boolean isUpdated = productsServices.updateProduct(product);
            if (isUpdated) {
                return ResponseEntity.ok().body(Map.of("message", "Product updated successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}