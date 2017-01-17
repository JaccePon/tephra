# 输出与模板
在默认情况下，输出结果为JSON格式的数据，如：
```java
package org.lpw.carousel.config;
 
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
 
/**
 * @author lpw
 */
@Controller("carousel.config.ctrl")
@Execute(name = "/carousel/config/")
public class ConfigCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private ConfigService configService;
 
    @Execute(name = "update")
    public Object update() {
        return configService.update(request.getFromInputStream()) ? templates.get().success(null, "carousel.config.update.success")
                : templates.get().failure(1001, message.get("carousel.config.update.failure"), null, null);
    }
}
```
其输出结果为：
```shell
curl http://localhost:8080/carousel/config/update
{"code":1001,"message":"更新配置失败，请检查配置值是否有误！","parameter":{}}
```
## 指定输出模板类型
通过设置@Execute的type属性，可以指定其它返回类型。如以下代码返回一个FreeMarker解析结果：
```java
package org.lpw.tephra.workbench.front;
 
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.util.Message;
import org.lpw.tephra.workbench.Suffix;
import org.lpw.tephra.workbench.ui.UiHelper;
import org.springframework.stereotype.Controller;
 
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author lpw
 */
@Controller("tephra.workbench.front" + Suffix.CTRL)
public class FrontCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private UiHelper uiHelper;
 
    @Execute(name = "/", type = Templates.FREEMARKER, template = "/tephra/workbench/front/index")
    public Object index() {
        Map<String, Object> map = new HashMap<>();
        map.put("menus", uiHelper.getMenus());
 
        return map;
    }
}
```
模板文件定义如下：
```freemarker
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="theme/easyui/metro/easyui.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/icon.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/color.css"/>
    <link rel="stylesheet" type="text/css" href="theme/tephra/default.css"/>
    <script type="text/javascript" src="script/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/jquery.easyui-1.4.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="script/tephra/util.js"></script>
    <script type="text/javascript" src="script/tephra/grid.js"></script>
    <script type="text/javascript" src="script/tephra/propertygrid.js"></script>
    <script type="text/javascript" src="script/tephra/workbench.js"></script>
    <script type="text/javascript" src="script/tephra/crud.js"></script>
</head>
<body class="easyui-layout">
<div data-options="region:'north'" style="height:50px">
    <div class="workbench-notice" style="display: none;"></div>
</div>
<div data-options="region:'south'" style="height:50px;"></div>
<div data-options="region:'west',title:' '" style="width:20%;">
    <div data-options="border:false" class="easyui-accordion" style="width:100%;height:100%;">
    <#list data.menus as menu>
        <div title="${menu.label}">
            <#list menu.children as child>
                <a class="menu-item" href="javascript:void(0);" onclick="javascript:tephra.workbench.menu('${menu.label}','${child.label}','${child.uri}');">${child.label}</a>
            </#list>
        </div>
    </#list>
    </div>
</div>
<div id="workbench" data-options="region:'center',title:' '">
</div>
<div id="workbench-confirm" class="easyui-dialog" style="width: 50%;height: 50%;" data-options="closed:true"></div>
<script type="text/javascript">
</script>
</body>
</html>
```
其输出结果为：
```html
curl http://localhost:8080/
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="theme/easyui/metro/easyui.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/icon.css"/>
    <link rel="stylesheet" type="text/css" href="theme/easyui/color.css"/>
    <link rel="stylesheet" type="text/css" href="theme/tephra/default.css"/>
    <script type="text/javascript" src="script/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/jquery.easyui-1.4.4.min.js"></script>
    <script type="text/javascript" src="script/jquery/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="script/tephra/util.js"></script>
    <script type="text/javascript" src="script/tephra/grid.js"></script>
    <script type="text/javascript" src="script/tephra/propertygrid.js"></script>
    <script type="text/javascript" src="script/tephra/workbench.js"></script>
    <script type="text/javascript" src="script/tephra/crud.js"></script>
</head>
<body class="easyui-layout">
<div data-options="region:'north'" style="height:50px">
    <div class="workbench-notice" style="display: none;"></div>
</div>
<div data-options="region:'south'" style="height:50px;"></div>
<div data-options="region:'west',title:' '" style="width:20%;">
    <div data-options="border:false" class="easyui-accordion" style="width:100%;height:100%;">
            <div title="系统设置">
                <a class="menu-item" href="javascript:void(0);" onclick="javascript:tephra.workbench.menu('系统设置','域管理','/tephra/workbench/domain/');">域管理</a>
                <a class="menu-item" href="javascript:void(0);" onclick="javascript:tephra.workbench.menu('系统设置','用户管理','/tephra/workbench/user/');">用户管理</a>
        </div>
    </div>
</div>
<div id="workbench" data-options="region:'center',title:' '">
</div>
<div id="workbench-confirm" class="easyui-dialog" style="width: 50%;height: 50%;" data-options="closed:true"></div>
<script type="text/javascript">
</script>
</body>
</html>
```
## 支持的输出模板
支持的模板可参考Templates：
```java
package org.lpw.tephra.ctrl.template;

/**
 * 模板管理器。用于获取模板实例。
 *
 * @author lpw
 */
public interface Templates {
    /**
     * JSON模板类型。
     */
    String JSON = "json";

    /**
     * FreeMarker模板类型。
     */
    String FREEMARKER = "freemarker";

    /**
     * String模板类型。
     */
    String STRING = "string";

    /**
     * Stream模板类型。
     */
    String STREAM = "stream";
}
```
## 自定义输出模板
如果系统自带的输出模板无法满足需求，可以自定义一个，只需实现以下接口即可：
```java
package org.lpw.tephra.ctrl.template;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 输出模板。
 *
 * @author lpw
 */
public interface Template {
    /**
     * 获取输出模板类型。
     *
     * @return 输出模板类型。
     */
    String getType();

    /**
     * 获取当前请求输出内容类型。
     *
     * @return 当前请求输出内容类型。
     */
    String getContentType();

    /**
     * 设置当前请求输出内容类型。
     *
     * @param contentType 当前请求输出内容类型。
     */
    void setContentType(String contentType);

    /**
     * 封装验证失败结果信息。
     *
     * @param code      验证失败编码。
     * @param message   验证失败错误信息。
     * @param parameter 验证参数名。
     * @param value     验证参数值。
     * @return 失败结果信息。
     */
    Object failure(int code, String message, String parameter, String value);

    /**
     * 封装执行成功返回结果说明。
     *
     * @param data 数据。
     * @param key  资源key。
     * @param args 参数值集。
     * @return 执行成功返回结果说明。
     */
    Object success(Object data, String key, Object... args);

    /**
     * 处理模版输出。
     *
     * @param name   模版文件名。
     * @param data   数据。
     * @param output 输出流。
     * @throws IOException 未处理IOException异常。
     */
    void process(String name, Object data, OutputStream output) throws IOException;
}
```
## 动态设置返回的模板文件名
通过注入TemplateHelper实现，可在运行时修改输出模板文件名：
```java
package org.lpw.tephra.ctrl.template;

/**
 * 模板支持。
 *
 * @author lpw
 */
public interface TemplateHelper {
    /**
     * 获取当前请求模板文件名。
     *
     * @return 当前请求模板文件名。
     */
    String getTemplate();

    /**
     * 设置当前请求模板文件名。
     *
     * @param template 当前请求模板文件名。
     */
    void setTemplate(String template);
}
```