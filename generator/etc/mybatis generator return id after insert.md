# MyBatis generator return id after insert
In this blog I will show you how to generate mapper that can return auto increase id after inserting. If you do not 
know how to use MyBatis generator you can click [here](http://www.henryxi.com/use-idea-mybatis-plugin-generate-mapper-files). 
You can also use maven plugin or other tools to generate mapper xml file and model class. 

**project structure**

```
├─main
│  ├─java
│  │  └─com
│  │      └─henryxi
│  │          └─mybatis
│  │              │  Client.java
│  │              │
│  │              ├─entity
│  │              │      UserInfoEntity.java
│  │              │      UserInfoEntityExample.java
│  │              │
│  │              └─mapper
│  │                      UserInfoEntityMapper.java
│  │ 
│  └─resources
│      │  mybatis-config.xml
│      │  mybatis-generator.xml
│      │
│      └─mapper
│              UserInfoEntityMapper.xml
│     
└─test
    └─java
```

**mybatis-generator.xml**

The most important part of configuration is `<generatedKey column="id" sqlStatement="MySql" identity="true"/>`. `<generatedKey>` 
means let DB generate auto increase id. You do not need pass id into entity. `identity` means For `<generatedKey>` detail you can see
[here](http://www.mybatis.org/generator/configreference/generatedKey.html). I do not found `<generatedKey>` support
PostgreSql ;(
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
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
                        connectionURL="jdbc:mysql://<YOUR_DB_ADDRESS>:3306/YOUR_DB_NAME" userId="USERNAME" password="PASSWORD"/>

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

        <!--&lt;!&ndash; !!!! Table Configurations !!!! &ndash;&gt;-->
        <!--<table tableName="users" modelType="flat" domainObjectName="UserInfoEntity">-->
            <!--<property name="ignoreQualifiersAtRuntime" value="true"/>-->
            <!--<property name="modelOnly" value="false"/>-->
        <!--</table>-->

        <!-- !!!! Table Configurations !!!! -->
        <table tableName="test_table" modelType="flat" domainObjectName="UserInfoEntity">
            <property name="ignoreQualifiersAtRuntime" value="true"/>
            <property name="modelOnly" value="false"/>
            <generatedKey column="id" sqlStatement="MySql" identity="true"/>
        </table>
    </context>
</generatorConfiguration>
```

**mybatis-config.xml**
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
                <property name="url" value="jdbc:mysql://<YOUR_DB_ADDRESS>:3306/YOUR_DB_NAME"/>
                <property name="username" value="USERNAME"/>
                <property name="password" value="PASSWORD"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mapper/UserInfoEntityMapper.xml"/>
    </mappers>
</configuration>
```

**Client.java**
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
```