# 单元测试覆盖率
1、添加JaCoCo：
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.7.7.201606060606</version>
            </plugin>
        </plugins>
    </build>
```
2、执行测试
```bash
mvn clean jacoco:prepare-agent test jacoco:report
```
3、查看报表
```
/target/site/
```
