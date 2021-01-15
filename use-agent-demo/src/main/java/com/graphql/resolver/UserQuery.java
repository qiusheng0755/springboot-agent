package com.graphql.resolver;

import com.pojo.Card;
import com.pojo.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @Author qiusheng
 * @Date 2021-1-15 9:28
 * @Version 1.0
 */
@Component
public class UserQuery implements GraphQLQueryResolver {

    private static HashMap<Integer, User> userMap = new HashMap<>();
    static {
        userMap.put(1, new User(1, "admin", 18, new Card("42010219821203", 1)));
        userMap.put(2, new User(1, "zhangsan", 23, new Card("44142118792334", 2)));
        userMap.put(3, new User(1, "lisi", 36, new Card("1212534532413", 3)));
        userMap.put(4, new User(1, "wangwu", 38, new Card("142342513111111", 4)));
    }

    public User getUserById(Integer id) {
        return userMap.get(id);
    }
}
