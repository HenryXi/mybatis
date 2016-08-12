package com.henryxi.pagination;

import com.github.pagehelper.PageHelper;
import com.henryxi.pagination.entity.UserInfoEntity;
import com.henryxi.pagination.entity.UserInfoEntityExample;
import com.henryxi.pagination.mapper.UserInfoEntityMapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        initData();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserInfoEntityMapper mapper = sqlSession.getMapper(UserInfoEntityMapper.class);
            UserInfoEntityExample example = new UserInfoEntityExample();
            UserInfoEntityExample.Criteria criteria = example.createCriteria();
            criteria.andIdGreaterThan(10);
            PageHelper.startPage(1, 10);
            List<UserInfoEntity> userInfoEntityList = mapper.selectByExample(example);
            System.out.println("pageNum:" + 1 + ",pageSize:" + 10);
            for (UserInfoEntity userInfoEntity : userInfoEntityList) {
                System.out.println(userInfoEntity.getUserName() + " " + userInfoEntity.getPassword());
            }
            sqlSession.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }

    private static void initData() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserInfoEntityMapper mapper = sqlSession.getMapper(UserInfoEntityMapper.class);
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            for (int i = 1; i <= 100; i++) {
                userInfoEntity.setUserName("User" + i);
                userInfoEntity.setPassword("User" + i + "_pwd");
                mapper.insert(userInfoEntity);
            }
            System.out.println("init finish!");
            sqlSession.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
        } finally {
            sqlSession.close();
        }
    }
}
