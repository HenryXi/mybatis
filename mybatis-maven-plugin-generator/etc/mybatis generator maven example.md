# Mybatis generator maven example
I have wrote a [tutorial](http://www.henryxi.com/use-idea-mybatis-plugin-generate-mapper-files) how to generator Mybatis
entity and mapper by using idea Mybatis plugin. In this blog I will how you how to generator Mybatis entity and mapper
by using maven plugin. Before using IDEA plugin you need pay for it, maven plugin is free instead.

**The structure of project**
```
├─main
│  ├─java
│  │  └─com
│  │      └─henryxi
│  │          └─mybatis
│  │              └─generator
│  │                  ├─entity
│  │                  └─mapper
│  └─resources
│          mybatis-generator.xml
│
└─test
    └─java
```
You need create the entity and mapper package before generating.

**The content of pom**
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
</dependencies>
```

**The content of `mybatis-generator`**
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE generatorConfiguration PUBLIC
        "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
<generatorConfiguration>

    <!-- !!!! Driver Class Path !!!! -->
    <classPathEntry location="C:\Users\Administrator\.IntelliJIdea2016.1\config\jdbc-drivers\mysql-connector-java-5.1.35-bin.jar"/>

    <context id="context" targetRuntime="MyBatis3">
        <commentGenerator>
            <property name="suppressAllComments" value="false"/>
            <property name="suppressDate" value="true"/>
        </commentGenerator>

        <!-- !!!! Database Configurations !!!! -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver" connectionURL="jdbc:mysql://localhost:3306/db" userId="root" password="123456"/>

        <javaTypeResolver>
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>

        <!-- !!!! Model Configurations !!!! -->
        <javaModelGenerator targetPackage="com.henryxi.mybatis.generator.entity" targetProject="src/main/java">
            <property name="enableSubPackages" value="false"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>

        <!-- !!!! Mapper XML Configurations !!!! -->
        <sqlMapGenerator targetPackage="com.henryxi.mybatis.generator.mapper" targetProject="src/main/resources">
            <property name="enableSubPackages" value="false"/>
        </sqlMapGenerator>

        <!-- !!!! Mapper Interface Configurations !!!! -->
        <javaClientGenerator targetPackage="com.henryxi.mybatis.generator.mapper" targetProject="src/main/java" type="XMLMAPPER">
            <property name="enableSubPackages" value="false"/>
        </javaClientGenerator>

        <!-- !!!! Table Configurations !!!! -->
        <table tableName="users" domainObjectName="User">
            <property name="ignoreQualifiersAtRuntime" value="true"/>
            <property name="modelOnly" value="false"/>
        </table>
    </context>
</generatorConfiguration>
```

Run `mvn mybatis-generator:generate` command it will generate entities and mapper for you. The sql of creating users
table like following.
```sql
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(500) NOT NULL,
  `pwd` varchar(500) NOT NULL,
  `email` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_user_name_uindex` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13611640 DEFAULT CHARSET=utf8
```

**After generating the structure like following**
```
├─main
│  ├─java
│  │  └─com
│  │      └─henryxi
│  │          └─mybatis
│  │              └─generator
│  │                  ├─entity
│  │                  │      User.java
│  │                  │      UserExample.java
│  │                  │
│  │                  └─mapper
│  │                          UserMapper.java
│  │
│  └─resources
│      │  mybatis-generator.xml
│      │
│      └─com
│          └─henryxi
│              └─mybatis
│                  └─generator
│                      └─mapper
│                              UserMapper.xml
│
└─test
    └─java
```

**How to use generated entity and mapper**

Add `mybatis-config.xml` in resources directory. The content of it like following.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <settings>
        <!-- Globally enables or disables any caches configured in any mapper under this configuration -->
        <setting name="cacheEnabled" value="true"/>
        <!-- Sets the number of seconds the driver will wait for a response from the database -->
        <setting name="defaultStatementTimeout" value="3000"/>
        <!-- Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names aColumn -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- Allows JDBC support for generated keys. A compatible driver is required.
        This setting forces generated keys to be used if set to true,
         as some drivers deny compatibility but still work -->
        <setting name="useGeneratedKeys" value="true"/>
    </settings>

    <!-- Continue going here -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/db"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com/henryxi/mybatis/generator/mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```
Use generated entity and mapper like following.
```java
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
```