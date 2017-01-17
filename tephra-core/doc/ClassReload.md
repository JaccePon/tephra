# Class热更新
虽然Tephra包含Class热更新能力，但仅建议在严重BUG修正、并且较小修改时使用，不建议用作版本更新，及频繁热更新。

1、配置core.tephra.config.properties：
```properties
## 设置重载类根路径。
## 如果设置为空则表示不动态重载。
#tephra.bean.reload.class-path =
```
2、将要更新的类（包含package结构）copy到${tephra.bean.reload.class-path}下。

3、在${tephra.bean.reload.class-path}下创建name文件，并将要更新的类写入name文件，如：
```txt
org.lpw.tephra.util.LoggerImpl
org.lpw.tephra.util.ValidatorImpl
```
载入时会按照name文件定义的顺序加载。

4、保存，系统会自动每分钟更新一次，载入完毕后会清空name文件。