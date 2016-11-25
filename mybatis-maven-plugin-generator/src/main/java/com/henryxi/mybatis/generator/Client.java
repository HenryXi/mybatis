package com.henryxi.mybatis.generator;

import com.henryxi.mybatis.generator.entity.User;
import com.henryxi.mybatis.generator.entity.UserExample;
import com.henryxi.mybatis.generator.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample example = new UserExample();
            example.createCriteria().andUserNameEqualTo("0000");
            List<User> allRecords = mapper.selectByExample(example);
            sqlSession.commit();
            for (User user : allRecords) {
                System.out.println("user id: " + user.getId());
                System.out.println("user name: " + user.getUserName());
            }
        } finally {
            sqlSession.close();
        }
    }
}
