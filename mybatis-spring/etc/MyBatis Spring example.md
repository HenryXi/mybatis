# MyBatis Spring example
In this page I will show you how to integrate MyBatis and Spring. I use maven to build the project. 
The structure of the project is like following. 
```
├─main
│  ├─java
│  │  └─com
│  │      └─henryxi
│  │          ├─controller
│  │          │      UserController.java
│  │          │
│  │          ├─entity
│  │          │      User.java
│  │          │
│  │          └─mapper
│  │                  UserMapper.java
│  │
│  ├─resources
│  │      application.xml
│  │      mybatis.xml
│  │
│  └─webapp
│      └─WEB-INF
│              web.xml
```
The content of web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
		  http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:application.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
The content of application.xml
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans-3.0.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <context:component-scan base-package="com.henryxi"/>
    <mvc:annotation-driven/>

    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="configLocation" value="classpath:mybatis.xml"/>
    </bean>

    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.henryxi.mapper"/>
    </bean>

    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <property name="driverClassName" value="com.mysql.jdbc.Driver">
        </property>
        <property name="url" value="jdbc:mysql://192.168.56.6:3306/test_db"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>

</beans>
```
The content of mybatis.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//ibatis.apache.org//DTD Config 3.1//EN"
        "http://ibatis.apache.org/dtd/ibatis-3-config.dtd">
<configuration>
    <properties>
        <property name="dialect" value="mysql"/>
    </properties>
</configuration>
```
For the java code they are all here
```java
@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/get")
    @ResponseBody
    public User get(@RequestParam("id") int id) {
        User user = userMapper.getUser(id);
        return user;
    }

    @ResponseBody
    @RequestMapping("/save")
    public String save(@RequestParam("id") int id, @RequestParam("name") String name) {
        userMapper.save(id, name);
        return "succ";
    }
}

public class User implements Serializable{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{userId}")
    User getUser(@Param("userId") int userId);

    @Insert("INSERT INTO users (id, name) VALUES (#{userId},#{userName}) ")
    void save(@Param("userId") int userId, @Param("userName") String userName);
}
```
Before you run this example you need to create database `test_db` or other name you like. Then create 
the table `users`. The sql of creating table is here.
```sql
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
```
After all steps are done. You can access 'localhost:8080/save?id=6&name=henry' to save user and access
'localhost:8080/get?id=6' to get the info of the user.

EOF