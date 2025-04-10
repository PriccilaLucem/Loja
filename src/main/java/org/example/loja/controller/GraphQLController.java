package org.example.loja.controller;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.loja.dto.ProductDTO;
import org.example.loja.services.ProductsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class GraphQLController {

    @Autowired
    private GraphQL graphQL;

    @GetMapping("/graphql")
    public ResponseEntity<Object> executeGraphQLQuery(
            @RequestParam String query,
            @RequestParam(required = false) Map<String, Object> variables) {
        try {
            ExecutionResult result = graphQL.execute(executionInput -> executionInput
                    .query(query)
                    .variables(variables != null ? variables : Map.of())
            );

            if (!result.getErrors().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "errors", result.getErrors()
                ));
            }

            return ResponseEntity.ok(result.getData());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error", "details", e.getMessage()));
        }
    }
}