package com.graphql;

import com.graphql.resolver.Mutation;
import com.graphql.resolver.Query;
import com.graphql.resolver.UserQuery;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;

import java.io.IOException;

/**
 * @Author qiusheng
 * @Date 2021-1-14 21:49
 * @Version 1.0
 */
public class GraphQLTest2 {
    /**
     * GraphQL 自带一组默认标量类型：
     * Int：有符号 32 位整数。
     * Float：有符号双精度浮点值。
     * String：UTF‐8 字符序列。
     * Boolean：true 或者 false。
     * ID：ID 标量类型表示一个唯一标识符，通常用以重新获取对象或者作为缓存中的键。ID 类型使用和 String 一样的方式序列化；然而将其定义为 ID 意味着并不需要人类可读型。
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //读取GraphQL文件，进行查询
        GraphQLSchema schema = SchemaParser.newParser()
                .file("common.graphqls")
                .resolvers(new Query(), new Mutation())
                .file("user.graphqls")
                .resolvers(new UserQuery())
                .build()
                .makeExecutableSchema();

        //根据Schema对象生成GraphQL对象
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();

        String query = "{getUserById(id:1){id,name,age,card{cardNumber,userId}}}";
        ExecutionResult result = graphQL.execute(query);

        System.out.println("query:" + query);
        System.out.println(result.toSpecification());
    }
}
