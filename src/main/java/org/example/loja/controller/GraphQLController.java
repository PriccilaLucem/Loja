package org.example.loja.controller;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for handling GraphQL queries and mutations.
 * Provides a single endpoint for executing GraphQL operations.
 */
@Tag(name = "GraphQL API", description = "Endpoint for executing GraphQL queries and mutations")
@RestController
@RequestMapping("/api/v1")
public class GraphQLController {

    @Autowired
    private GraphQL graphQL;

    private static  final Logger logger = LoggerFactory.getLogger(GraphQLController.class);
    @Operation(
            summary = "Execute GraphQL query",
            description = """
            Executes a GraphQL query or mutation. The GraphQL schema supports the following operations:
            
            **Queries:**
            - products: Retrieve all products
            - productById(id: ID!): Retrieve a single product by ID
            - productsByCategory(category: String!): Retrieve products by category
            
            **Types:**
            ```graphql
            type Product {
                id: ID
                name: String
                price: Float
                description: String
                image: String
                brand: String
                quantity: Int
                store: Store
                categories: [Category]
            }
            
            type Store {
                id: ID
                name: String
                address: String
                owner: String
            }
            
            type Category {
                id: ID
                name: String
                description: String
            }
            ```
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful GraphQL execution",
                            content = @Content(schema = @Schema(implementation = Object.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "GraphQL execution errors",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            })
        @PostMapping("/graphql")  // Changed to POST
        public ResponseEntity<Object> executeGraphQLQuery(
                @RequestBody Map<String, Object> request) {  // Changed to RequestBody

            try {
                if (request == null || !request.containsKey("query")) {
                    logger.error("GraphQL request must contain a 'query' field");
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "GraphQL request must contain a 'query' field"));
                }

                String query = (String) request.get("query");
                Map<String, Object> variables = (Map<String, Object>) request.getOrDefault("variables", Collections.emptyMap());

                ExecutionResult result = graphQL.execute(ExecutionInput.newExecutionInput()
                        .query(query)
                        .variables(variables)
                        .build());

                if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                    logger.error("GraphQL errors: {}", result.getErrors());
                    return ResponseEntity.badRequest().body(Map.of("errors", result.getErrors()));
                }

                logger.info("GraphQL result: {}", Optional.ofNullable(result.getData()));
                return ResponseEntity.ok(result.toSpecification());
            } catch (Exception e) {
                logger.error("GraphQL execution error: {}", e.getMessage());
                return ResponseEntity.internalServerError().body(Map.of(
                        "error", "Internal Server Error",
                "message", e.getMessage()));
            }
    }
}
