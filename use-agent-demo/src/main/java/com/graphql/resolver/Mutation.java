package com.graphql.resolver;


import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

/**
 * Root mutation resolver.
 */
@Component
public class Mutation implements GraphQLMutationResolver {
    private String version = "8.0";
}
