# MyBatis first level cache example
The first level cache is enabled by default in MyBatis. It will cache the result after querying data from database. The second
time you query MyBatis won't hit db. The first level cache is for session. If the session is close the cache will be cleared.
I assume that you have create database and insert some data in your table.

**table create sql**
```sql
CREATE TABLE users
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(50),
  password VARCHAR(50)
);
```
The sample code for testing the first level cache is like following.

**Structure of project**
```
├─main
│  ├─java
│  │  └─com
│  │      └─henryxi
│  │          └─cache
│  │              │  Client.java
│  │              │
│  │              ├─entity
│  │              │      User.java
│  │              │
│  │              └─mapper
│  │                      UserMapper.java
│  │
│  └─resources
│      │  log4j.xml
│      │  mybatis-config.xml
│      │
│      └─com
│          └─henryxi
│              └─cache
│                  └─mapper
│                          UserMapper.xml
│
└─test
    └─java
```

**content of pom.xml**
```xml
<dependencies>
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
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
</dependencies>
```

**UserMapper.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.henryxi.cache.mapper.UserMapper">
    <resultMap id="BaseResultMap" type="com.henryxi.cache.entity.User">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="user_name" jdbcType="VARCHAR" property="userName"/>
        <result column="password" jdbcType="VARCHAR" property="password"/>
    </resultMap>
    <select id="selectByIdWithCache" parameterType="java.lang.Integer" resultMap="BaseResultMap">
        select id,user_name,password from users where id = #{id,jdbcType=INTEGER}
    </select>
    <select id="selectByIdWithoutCache" parameterType="java.lang.Integer" resultMap="BaseResultMap" flushCache="true">
        select id,user_name,password from users where id = #{id,jdbcType=INTEGER}
    </select>
</mapper>
```
**log4j.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
    <appender name="console" class="org.apache.log4j.ConsoleAppender">
        <param name="Target" value="System.out"/>
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern"
                   value="%d{yyyy-MM-dd HH:mm:ss} %p [%t] %C{1}.%M(%F:%L) - %m%n"/>
        </layout>
    </appender>

    <logger name="com.henryxi.cache.mapper" additivity="false">
        <level value="DEBUG"/>
        <appender-ref ref="console"/>
    </logger>

    <root>
        <priority value="error"/>
        <appender-ref ref="console"/>
    </root>
</log4j:configuration>
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

    <!-- Continue going here -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://<your_db_host>:<your_db_port>/<your_db_name>"/>
                <property name="username" value="<your_db_username>"/>
                <property name="password" value="<your_db_password>"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com/henryxi/cache/mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```
**java code**
```java
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


public interface UserMapper {
    User selectByIdWithCache(Integer id);

    User selectByIdWithoutCache(Integer id);
}

public class User {
    private Integer id;
    private String userName;
    private String password;

    //getter and setter methods
}
```

Execute the main method of `Client` the output is like following.
```
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==>  Preparing: select id,user_name,password from users where id = ? 
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==> Parameters: 216(Integer)
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - <==      Total: 1
first query with cache:User15_update_again
second query with cache:User15_update_again
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==>  Preparing: select id,user_name,password from users where id = ? 
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==> Parameters: 216(Integer)
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - <==      Total: 1
first query without cacheUser15_update_again
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==>  Preparing: select id,user_name,password from users where id = ? 
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - ==> Parameters: 216(Integer)
2017-11-13 14:25:31 DEBUG [main] BaseJdbcLogger.debug(BaseJdbcLogger.java:139) - <==      Total: 1
second query without cacheUser15_update_again
```
The second query with cache don't query in database(no sql log output). The first and second query without cache will query
data in database(There are two query log output).
 
* MyBatis will cache the query result as default. 
* MyBatis will query database every time after adding `flushCache="true"` in mapper xml file.