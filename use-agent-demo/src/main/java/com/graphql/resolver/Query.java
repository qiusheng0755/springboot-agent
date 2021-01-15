package com.graphql.resolver;


import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

/**
 * Root Query Resolver.
 */
@Component
public class Query implements GraphQLQueryResolver {
    private String version = "8.0";
}
