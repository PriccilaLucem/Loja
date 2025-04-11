package org.example.loja.controller;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
    /**
     * Executes a GraphQL query or mutation.
     *
     * @param query The GraphQL query/mutation string
     * @param variables Optional variables for the query/mutation
     * @return The result of the GraphQL execution
     */
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
                    @GetMapping("/graphql")
    public ResponseEntity<Object> executeGraphQLQuery(
            @Parameter(
                    description = "GraphQL query/mutation string",
                    required = true,
                    examples = {
                            @ExampleObject(
                                    name = "Get all products",
                                    value = "{ products { id name price } }"),
                            @ExampleObject(
                                    name = "Get product by ID",
                                    value = "query($id: ID!) { productById(id: $id) { name price description } }"),
                            @ExampleObject(
                                    name = "Get products by category",
                                    value = "query($category: String!) { productsByCategory(category: $category) { id name } }")
                    })
            @RequestParam String query,

            @Parameter(
                    description = "Variables for the GraphQL query/mutation",
                    example = "{\"id\": \"123\", \"category\": \"electronics\"}")
            @RequestParam(required = false) Map<String, Object> variables) {

        try {
            ExecutionResult result = graphQL.execute(executionInput -> executionInput
                    .query(query)
                    .variables(variables != null ? variables : Map.of())
            );

            if (!result.getErrors().isEmpty()) {
                logger.error(result.getErrors().toString());
                return ResponseEntity.badRequest().body(Map.of(
                        "errors", result.getErrors()
                ));
            }
            logger.info("GraphQL result: {}", result.getData().toString());

            return ResponseEntity.ok(result.getData());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Internal Server Error",
                    "details", e.getMessage()));
        }
    }
}