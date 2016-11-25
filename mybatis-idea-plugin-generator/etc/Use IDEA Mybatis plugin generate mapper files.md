# Use IDEA Mybatis plugin generate mapper files
Mybatis is a light weight Java persistence framework(compared with Hibernate). You have to write
a lot of mapper xml files and mapper interfaces. These things are duplicate works, we can generate
them by IDEA Mybatis plugin. In this page I will show you how to use this plugin to generate xml mapper
file and mapper interface.

1. install Mybatis plugin

    Search Mybatis plugin in Intellij repositories and click install.
    ![IDEA Mybatis plugin](http://a.disquscdn.com/uploads/mediaembed/images/3829/5262/original.jpg)
2. create mybatis-generator-config file

    Right click resources folder choose **New** | **mybatis-generator-config**. I named this file "mybatis-generator.xml"
    you can choose any name you like.
    
    ![IDEA Mybatis plugin](http://a.disquscdn.com/uploads/mediaembed/images/3829/6805/original.jpg)
3. the content of mybatis-generator.xml like following. 
    
    ```xml
    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE generatorConfiguration PUBLIC
            "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
            "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd" >
    <generatorConfiguration>
    
        <!-- !!!! Driver Class Path !!!! -->
        <classPathEntry location="C:\Users\Administrator\.IntelliJIdea15\config\jdbc-drivers\postgresql-9.4-1201.jdbc4.jar"/>
    
        <context id="context" targetRuntime="MyBatis3">
            <commentGenerator>
                <property name="suppressAllComments" value="false"/>
                <property name="suppressDate" value="true"/>
            </commentGenerator>
    
            <!-- !!!! Database Configurations !!!! -->
            <jdbcConnection driverClass="org.postgresql.Driver"
                            connectionURL="jdbc:postgresql://localhost:5432/users" userId="postgres" password="postgres"/>
    
            <javaTypeResolver>
                <property name="forceBigDecimals" value="false"/>
            </javaTypeResolver>
    
            <!-- !!!! Model Configurations !!!! -->
            <javaModelGenerator targetPackage="com.henryxi.mybatis.entity" targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED">
                <property name="enableSubPackages" value="false"/>
                <property name="trimStrings" value="true"/>
            </javaModelGenerator>
    
            <!-- !!!! Mapper XML Configurations !!!! -->
            <sqlMapGenerator targetPackage="mapper" targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED">
                <property name="enableSubPackages" value="false"/>
            </sqlMapGenerator>
    
            <!-- !!!! Mapper Interface Configurations !!!! -->
            <javaClientGenerator targetPackage="com.henryxi.mybatis.mapper" targetProject="THIS_CONFIGURATION_IS_NOT_REQUIRED"
                                 type="XMLMAPPER">
                <property name="enableSubPackages" value="false"/>
            </javaClientGenerator>
    
            <!-- !!!! Table Configurations !!!! -->
            <table tableName="users" modelType="flat" domainObjectName="UserInfoEntity">
                <property name="ignoreQualifiersAtRuntime" value="true"/>
                <property name="modelOnly" value="false"/>
            </table>
        </context>
    </generatorConfiguration>
    ```

4. This plugin won't create package you need create package and folder manually. The structure of this demo at
    the end of this page.
    
5. Right click "mybatis-generator" and choose **Run as Mybatis Generator**. After generating move the mapper xml 
files to resource folder.
    
    ![run config](http://a.disquscdn.com/uploads/mediaembed/images/3830/8797/original.jpg)

6. the structure of this demo like following.

    ```
    └─main
        ├─java
        │  └─com
        │      └─henryxi
        │          └─mybatis
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
7. the code of java and mybatis config

    ```java
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
            } finally {
                sqlSession.close();
            }
        }
    }
    ```
    
    mybatis-config.xml
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
                    <property name="driver" value="org.postgresql.Driver"/>
                    <property name="url" value="jdbc:postgresql://localhost:5432/users"/>
                    <property name="username" value="postgres"/>
                    <property name="password" value="postgres"/>
                </dataSource>
            </environment>
        </environments>
        <mappers>
            <mapper resource="mapper/UserInfoEntityMapper.xml"/>
        </mappers>
    </configuration>
    ```