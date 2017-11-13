package com.henryxi.cache;

import com.henryxi.cache.entity.User;
import com.henryxi.cache.mapper.UserMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class Client {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public static void main(String[] args) throws IOException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            User firstSelectWithCache = mapper.selectByIdWithCache(216);
            System.out.println("first query with cache:" + firstSelectWithCache.getUserName());
            User secondSelectWithCache = mapper.selectByIdWithCache(216);
            System.out.println("second query with cache:" + secondSelectWithCache.getUserName());
            User firstSelectWithoutCache = mapper.selectByIdWithoutCache(216);
            System.out.println("first query without cache" + firstSelectWithoutCache.getUserName());
            User secondSelectWithoutCache = mapper.selectByIdWithoutCache(216);
            System.out.println("second query without cache" + secondSelectWithoutCache.getUserName());
            sqlSession.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}
