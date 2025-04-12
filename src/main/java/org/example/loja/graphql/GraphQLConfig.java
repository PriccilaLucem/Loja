package org.example.loja.graphql;

import graphql.Scalars;
import graphql.language.IntValue;
import graphql.schema.*;
import org.example.loja.services.ProductsServices;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import graphql.GraphQL;

@Configuration
public class GraphQLConfig {

    @Autowired
    private ProductsServices productsServices;

    @Bean
    public GraphQL graphQL() {
        GraphQLScalarType longScalar = GraphQLScalarType.newScalar()
                .name("Long")
                .description("Custom Scalar for Java Long")
                .coercing(new Coercing<Long, Long>() {
                    @Override
                    public Long serialize(@NotNull Object dataFetcherResult) {
                        return ((Number) dataFetcherResult).longValue();
                    }

                    @Override
                    public Long parseValue(@NotNull Object input) {
                        if (input instanceof Number) {
                            return ((Number) input).longValue();
                        }
                        throw new IllegalArgumentException("Value is not a valid Long");
                    }

                    @Override
                    public Long parseLiteral(@NotNull Object input) {
                        if (input instanceof IntValue) {
                            return ((IntValue) input).getValue().longValue();
                        }
                        throw new IllegalArgumentException("Value is not a valid Long literal");
                    }
                })
                .build();

        GraphQLObjectType categoryType = GraphQLObjectType.newObject()
                .name("Category")
                .field(field -> field.name("id").type(longScalar))
                .field(field -> field.name("name").type(Scalars.GraphQLString))
                .field(field -> field.name("description").type(Scalars.GraphQLString))
                .build();

        GraphQLObjectType storeType = GraphQLObjectType.newObject()
                .name("Store")
                .field(field -> field.name("id").type(longScalar))
                .field(field -> field.name("name").type(Scalars.GraphQLString))
                .field(field -> field.name("address").type(Scalars.GraphQLString))
                .field(field -> field.name("owner").type(Scalars.GraphQLString))
                .build();

        GraphQLObjectType productType = GraphQLObjectType.newObject()
                .name("Product")
                .field(field -> field.name("id").type(longScalar))
                .field(field -> field.name("name").type(Scalars.GraphQLString))
                .field(field -> field.name("price").type(Scalars.GraphQLFloat))
                .field(field -> field.name("description").type(Scalars.GraphQLString))
                .field(field -> field.name("image").type(Scalars.GraphQLString)) // URL da imagem do produto
                .field(field -> field.name("brand").type(Scalars.GraphQLString)) // Marca do produto
                .field(field -> field.name("quantity").type(Scalars.GraphQLInt)) // Quantidade no estoque
                .field(field -> field.name("store").type(storeType)) // Ligação com a loja
                .field(field -> field.name("categories").type(new GraphQLList(categoryType))) // Lista de categorias
                .build();

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                .name("Query")
                .field(field -> field
                        .name("products")
                        .description("List all products")
                        .type(new GraphQLList(productType))
                        .dataFetcher(environment -> productsServices.getAllProducts())) // DataFetcher para todos os produtos
                .field(field -> field
                        .name("productById")
                        .description("Find a product by its ID")
                        .argument(arg -> arg.name("id").type(longScalar))
                        .type(productType)
                        .dataFetcher(environment -> productsServices.getProductById(
                                environment.getArgument("id")))) // Busca produto por ID
                .field(field -> field
                        .name("productsByCategory")
                        .description("Find products by category name")
                        .argument(arg -> arg.name("categoryName").type(Scalars.GraphQLString))
                        .type(new GraphQLList(productType))
                        .dataFetcher(environment -> productsServices.getProductsByCategories(
                                environment.getArgument("categoryName"))))
                .build();

        GraphQLSchema graphQLSchema = GraphQLSchema.newSchema()
                .query(queryType)
                .build();

        return GraphQL.newGraphQL(graphQLSchema).build();
    }
}