# MyBatis pagination example
In this blog I will show you how to get paged query result with MyBatis plugin [pagehelper](https://github.com/pagehelper/Mybatis-PageHelper).
If you are interested in it see the link for more detail. I use MyBatis generator plugin to generate mapper and xml file.
If you do not know how to use it click [here](http://www.henryxi.com/use-idea-mybatis-plugin-generate-mapper-files)

**project structure**
```
└─main
    ├─java
    │  └─com
    │      └─henryxi
    │          └─pagination
    │              │  Client.java
    │              │
    │              ├─entity
    │              │      UserInfoEntity.java
    │              │      UserInfoEntityExample.java
    │              │
    │              └─mapper
    │                      UserInfoEntityMapper.java
    │
    └─resources
        │  mybatis-config.xml
        │  mybatis-generator.xml
        │
        └─mapper
                UserInfoEntityMapper.xml
```

**pom.xml**

This plugin (pagehelper) support many databases I choose MySQL you can use any one of them.
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.38</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
</dependency>
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>4.1.3</version>
</dependency>
```
**create database and table**
```sql
CREATE DATABASE test_db;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
```
**Client.java**
```
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
            PageHelper.startPage(1, 10, false);
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
```

**mybatis-config.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <setting name="cacheEnabled" value="true"/>
        <setting name="defaultStatementTimeout" value="3000"/>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <setting name="useGeneratedKeys" value="true"/>
    </settings>
    <plugins>
        <plugin interceptor="com.github.pagehelper.PageHelper">
            <property name="dialect" value="mysql"/>
            <property name="offsetAsPageNum" value="true"/>
            <property name="rowBoundsWithCount" value="true"/>
            <property name="pageSizeZero" value="true"/>
            <property name="reasonable" value="false"/>
            <property name="params" value="pageNum=pageHelperStart;pageSize=pageHelperRows;"/>
            <property name="supportMethodsArguments" value="false"/>
            <property name="returnPageInfo" value="none"/>
        </plugin>
    </plugins>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://<YOUR_DB_ADDRESS>:3306/test_db"/>
                <property name="username" value="<DB_USERNAME>"/>
                <property name="password" value="<DB_PASSWORD>"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mapper/UserInfoEntityMapper.xml"/>
    </mappers>
</configuration>
```

In order to use pagehelper plugin you need add plugin node in MyBatis configuration file. After adding plugin configuration
to `mybatis-config.xml` you need add `PageHelper.startPage(1, 10);` in your code. This means the result will be paging.
the output will like following.
```
init finish!
pageNum:1,pageSize:10
User1 User1_pwd
User2 User2_pwd
User3 User3_pwd
User4 User4_pwd
User5 User5_pwd
User6 User6_pwd
User7 User7_pwd
User8 User8_pwd
User9 User9_pwd
User10 User10_pwd
```