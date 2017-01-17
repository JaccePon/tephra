# Core简介
Core模块主要提供基础功能封装，包含Bean的管理、缓存的使用、NIO支持、定时器控制等功能；同时提供了常用功能的工具包，以简化开发过程中的重复代码。
- bean——Bean管理包，主要实现Bean的管理，包括在非Spring容器中获取Bean、Class热更新、Spring容器状态监听等功能。
- nio——NIO功能包，提供基于NIO的Server、Client支持，简化项目中的NIO开发。
- cache——提供缓存管理功能，可使用全冗余缓存或集于Redis的集中式缓存管理；全冗余缓存节点间采用NIO建立长连接同步数据，并且自动发现同一网段的节点，以及断开自动重联。
- scheduler——提供四个标准的定时器及定时任务接口，可以满足大部分情况下的计划任务需求。
- crypto——提供加解密算法。
- freemarker——提供基于FreeMarker的模板文件解析。
- util——提供部分常用功能封装，具体功能可参考接口说明。

[BeanFactory](doc/BeanFactory.md)

[Class热更新](doc/ClassReload.md)

[NIO Server & Client](doc/nio.md)

[缓存](doc/cache.md)

[定时器 & 定时任务](doc/scheduler.md)

[util工具包](doc/util.md)

[spring.xml](doc/spring.md)

[log4j2.xml](doc/log4j2.md)

[配置参数](src/main/resources/core.tephra.config)