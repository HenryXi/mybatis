# MyBatis show sql log example
Show sql log can help you know the detail of your sql when you use MyBatis. I will show you how to show the sql
log when you use MyBatis. The content of pom.xml is like following.

**The structure of project**
```
├─java
│  └─com
│      └─henryxi
│          │  Client.java
│          │
│          └─mybatis
│              └─log
│                  ├─entity
│                  │      User.java
│                  │      UserExample.java
│                  │
│                  └─mapper
│                          UserMapper.java
│
└─resources
    │  log4j.properties
    │  mybatis-config.xml
    │  mybatis-generator.xml
    │
    └─com
        └─henryxi
            └─mybatis
                └─log
                    └─mapper
                            UserMapper.xml
```
**pom.xml**

User `mybatis-generator` to generate `User.java`,`UserExample.java`,`UserMapper.java` and `UserMapper.xml`. If
 you do not know how to use it click [Mybatis generator maven example](http://www.henryxi.com/mybatis-generator-maven-example) for more detail.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>1.3.5</version>
            <configuration>
                <configurationFile>${basedir}/src/main/resources/mybatis-generator.xml</configurationFile>
                <overwrite>true</overwrite>
            </configuration>
        </plugin>
    </plugins>
</build>
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

**log4j configuration**
If you use log4j.properties the content is almost like following.
```ini
# Global logging configuration
log4j.rootLogger=ERROR, stdout
# MyBatis logging configuration...
log4j.logger.com.henryxi.mybatis.log.mapper.UserMapper=DEBUG #### <==== define the log level for your own mapper 
# Console output...
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ISO8601} %p [%t] %C{1}.%M(%F:%L) - %m%n
```
If you use log4j.xml the content is almost like following.
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

    <logger name="com.henryxi.mybatis.log.mapper" additivity="false"> <!-- <==== define the log level for your own mapper -->
        <level value="DEBUG"/>
        <appender-ref ref="console"/>
    </logger>

    <root>
        <priority value="error"/>
        <appender-ref ref="console"/>
    </root>
</log4j:configuration>
```

**Table schema**
```sql
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
```

**Client.java**
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
        initData();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andIdLessThan(10);
            List<User> userList = mapper.selectByExample(example);
            for (User user : userList) {
                System.out.println(user.getUserName() + " " + user.getPassword());
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
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            UserExample example = new UserExample();
            long count = mapper.countByExample(example);
            if (count > 0) return;
            User user = new User();
            for (int i = 1; i <= 100; i++) {
                user.setUserName("User" + i);
                user.setPassword("User" + i + "_pwd");
                mapper.insert(user);
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