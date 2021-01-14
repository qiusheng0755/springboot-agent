package com.graphql;

import com.pojo.Card;
import com.pojo.User;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * @Author qiusheng
 * @Date 2021-1-14 21:49
 * @Version 1.0
 */
public class GraphQLTest {
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
        String fileName = "user.graphqls";
        String schema = IOUtils.toString(GraphQLTest.class.getClassLoader().getResource(fileName), "UTF-8");
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schema);

        //解决的是数据的查询
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("UserQuery", builder ->
                    builder.dataFetcher("user", environment -> {
                        Integer id = environment.getArgument("id");
                        Card card = new Card("4201021982120333312", id);
                        return new User(id, "张三:"+id, 20+id.intValue(), card);
                    })
                )
                .build();

        //生成Schema
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        //根据Schema对象生成GraphQL对象
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        String query = "{user(id:100000){id,name,age,card{cardNumber,userId}}}";
        ExecutionResult result = graphQL.execute(query);

        System.out.println("query:" + query);
        System.out.println(result.toSpecification());
    }
}
