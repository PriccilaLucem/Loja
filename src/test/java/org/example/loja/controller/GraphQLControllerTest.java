package org.example.loja.controller;

import graphql.*;
import graphql.language.SourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GraphQLControllerTest {

    @InjectMocks
    private GraphQLController graphQLController;

    @Mock
    private GraphQL graphQL;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the mocked logger
    }

    @Test
    void testGraphQLQuery_Success() {
        // Arrange
        Map<String, Object> request = Map.of(
                "query", "{ products { id name } }",
                "variables", Collections.emptyMap()
        );

        ExecutionResult executionResult = mock(ExecutionResult.class);
        when(executionResult.getData()).thenReturn(Map.of("products", List.of()));
        when(executionResult.getErrors()).thenReturn(Collections.emptyList());
        when(executionResult.toSpecification()).thenReturn(Map.of("data", Map.of("products", List.of())));
        when(graphQL.execute(any(ExecutionInput.class))).thenReturn(executionResult);

        // Act
        ResponseEntity<Object> response = graphQLController.executeGraphQLQuery(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    @Test
    void testGraphQLQuery_MissingQuery() {
        // Arrange
        Map<String, Object> request = Map.of("variables", Collections.emptyMap());

        // Act
        ResponseEntity<Object> response = graphQLController.executeGraphQLQuery(request);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    @Test
    void testGraphQLQuery_WithVariables() {
        // Arrange
        Map<String, Object> request = Map.of(
                "query", "query($id: ID!) { productById(id: $id) { name } }",
                "variables", Map.of("id", "123")
        );

        ExecutionResult executionResult = mock(ExecutionResult.class);
        when(executionResult.getData()).thenReturn(Map.of("productById", Map.of("name", "Test Product")));
        when(executionResult.getErrors()).thenReturn(Collections.emptyList());
        when(executionResult.toSpecification()).thenReturn(Map.of("data", Map.of("productById", Map.of("name", "Test Product"))));
        when(graphQL.execute(any(ExecutionInput.class))).thenReturn(executionResult);

        // Act
        ResponseEntity<Object> response = graphQLController.executeGraphQLQuery(request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("data"));
    }

    @Test
    void testGraphQLQuery_WithErrors() {
        // Arrange
        Map<String, Object> request = Map.of(
                "query", "{ invalidField }",
                "variables", Collections.emptyMap()
        );

        ExecutionResult executionResult = mock(ExecutionResult.class);
        when(executionResult.getErrors()).thenReturn(List.of(
                new GraphQLError() {
                    @Override public String getMessage() { return "Validation error"; }
                    @Override public List<SourceLocation> getLocations() { return List.of(); }
                    @Override public ErrorClassification getErrorType() { return null; }
                }
        ));
        when(graphQL.execute(any(ExecutionInput.class))).thenReturn(executionResult);

        // Act
        ResponseEntity<Object> response = graphQLController.executeGraphQLQuery(request);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("errors"));
    }

    @Test
    void testGraphQLQuery_WithExecutionException() {
        // Arrange
        Map<String, Object> request = Map.of(
                "query", "{ products { id } }",
                "variables", Collections.emptyMap()
        );

        when(graphQL.execute(any(ExecutionInput.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Object> response = graphQLController.executeGraphQLQuery(request);

        // Assert
        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Database error"));
    }
}