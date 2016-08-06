package com.henryxi.mybatis;

import com.henryxi.mybatis.entity.UserInfoEntity;
import com.henryxi.mybatis.mapper.UserInfoEntityMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class Client {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserInfoEntityMapper mapper = sqlSession.getMapper(UserInfoEntityMapper.class);
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setName("HenryXi");
            mapper.insert(userInfoEntity);
            System.out.println(userInfoEntity.getId());
            sqlSession.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}

