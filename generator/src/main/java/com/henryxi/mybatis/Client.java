package com.henryxi.mybatis;

import com.henryxi.mybatis.entity.UserInfoEntity;
import com.henryxi.mybatis.entity.UserInfoEntityExample;
import com.henryxi.mybatis.mapper.UserInfoEntityMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class Client {
    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserInfoEntityMapper mapper = sqlSession.getMapper(UserInfoEntityMapper.class);
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setId(UUID.randomUUID().toString());
            userInfoEntity.setName("HenryXi");
            mapper.insert(userInfoEntity);
            UserInfoEntityExample example = new UserInfoEntityExample();
            example.createCriteria().andNameEqualTo("HenryXi");
            List<UserInfoEntity> allRecords = mapper.selectByExample(example);
            sqlSession.commit();
        } catch (PersistenceException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof PSQLException) {
                // handle the exception
            }
        } finally {
            sqlSession.close();
        }
    }
}

