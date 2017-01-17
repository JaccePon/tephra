# 导出Excel

使用Excel模板可以将数据导出为Excel文件并下载。如：
```java
package org.lpw.tephra.ctrl.template.excel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.template.Templates;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.excel.ctrl")
@Execute(name = "/ctrl/template/excel/")
public class ExcelCtrl {
    @Execute(name = "download", type = Templates.EXCEL)
    public Object download() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < 10; i++) {
            JSONObject object = new JSONObject();
            object.put("sort", i);
            object.put("name", "name " + i);
            array.add(object);
        }

        return BeanFactory.getBean(ExcelBuilder.class).build(new String[]{"顺序", "名称"}, new String[]{"sort", "name"}, array)
                .download("下载");
    }
}
```
在浏览器中执行http://localhost:8080/ctrl/template/excel/download时，将下载名为【下载.xls】的Excel文件。
