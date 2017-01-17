# 上传文件
CtrlHttp提供了文件上传实现。

1、配置http.ctrl.tephra.config.properties设置允许上传的最大文件大小。
```properties
## 设置上传配置文件路径。
## 此目录下的配置每分钟自动更新。
## 文件名为监听key+.json。
## 配置可参考key.json。
#tephra.ctrl.http.upload.json-configs = /WEB-INF/upload
## 设置文件上传最大文件大小。
#tephra.ctrl.http.upload.max-size = 1m
```
2（Java）、实现UploadListener以监听文件上传事件，文件上传完成时将调用此接口实现。
```java
package org.lpw.tephra.ctrl.http.upload;

/**
 * @author lpw
 */
public interface UploadListener {
    /**
     * 获取监听器key；监听器key必须与上传字段名一致。
     *
     * @return 监听器key，支持正则表达式。
     */
    String getKey();

    /**
     * 验证是否允许上传。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 如果允许则返回true；否则返回false。
     */
    boolean isUploadEnable(String key, String contentType, String name);

    /**
     * 获取文件保存路径。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 文件保存路径。
     */
    default String getPath(String key, String contentType, String name) {
        return "";
    }

    /**
     * 获取图片大小。
     * 当上传文件为图片时，并且返回的图片大小（长、高）大于0时，自动将图片修改为长宽不超过设置值的图片，并进行压缩。
     *
     * @param key 上传文件key。
     * @return 图片大小[长, 高]，如果返回空或0集则表示不需要调整图片。
     */
    default int[] getImageSize(String key) {
        return null;
    }

    /**
     * 获取存储处理器。
     *
     * @return 存储处理器。
     */
    default String getStorage() {
        return null;
    }

    /**
     * 处理上传信息。
     *
     * @param key  上传文件key。
     * @param name 文件名。
     * @param size 文件大小。
     * @param uri  文件URI地址。
     * @return 输出结果。
     */
    String upload(String key, String name, String size, String uri);
}
```
2（JSON）、也可在${tephra.ctrl.http.upload.json-configs}目录下添加如下json配置（文件名为：监听器key+.json）。
```json
{
  "path-comment": "文件类型保存路径。key为文件类型（Content Type），支持正则表达式；value为保存的路径。",
  "path": {
    "image/.+": ""
  },
  "image-size-comment": "图片大小[宽,高]。",
  "image-size": [0,0]
}
```
3、客户端上传文件。
```html
<input id="fileupload" type="file" name="key" data-url="/tephra/ctrl-http/upload" multiple>
<script type="text/javascript">
    $(function () {
        $("#fileupload").fileupload({
            progress: function (e, data) {
                $("input").attr("disabled", "disabled");
                $(".fileinput-button").attr("disabled", "disabled");
                var percent = Math.round(data.loaded / data.total * 100);
                $(".progress .progress-bar").html(percent + "%");
                $(".progress .progress-bar").width(percent + "%");
            },
            done: function (e, data) {
                spark.workbench.load("/spark/scrum/folder/query");
            }
        });
    });
</script>
```
## 对上传的文件进行分类存储
上传文件默认保存为/upload/${content-type}/${date}/${file-name}，其中${content-type}为文件类型，如image/png，${date}为上传日期，格式为yyyyMMdd，${file-name}为随机生成的长度为32个字符的文件名＋文件后缀。

如果需要对上传的文件分门别类存储到不同的路径下，可以重写getPath方法，返回目标分类地址即可。如返回/${path}/，则文件最终保存的路径为：/upload/${content-type}/${path}/${date}/${file-name}。