# spring.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd">
    <aop:aspectj-autoproxy proxy-target-class="true"/>
    <context:component-scan base-package="org.lpw"/>
    <context:property-placeholder location="classpath*:*.config"/>
</beans>
```
context:component-scan配置自动搜索的类范围，可根据实际项目需求添加多个搜索范围。