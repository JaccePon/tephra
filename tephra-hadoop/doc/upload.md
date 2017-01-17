# 上传文件到Hadppp

实现UploadListener时，只需指定storage为hdfs，则文件将被直接上传到Hadoop。如：
```java
package org.lpw.tephra.ctrl.http.upload;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.http.upload-listener.hdfs")
public class HdfsUploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return "upload.hdfs";
    }

    @Override
    public boolean isUploadEnable(String key, String contentType, String name) {
        return true;
    }

    @Override
    public String getStorage() {
        return "hdfs";
    }

    @Override
    public String upload(String key, String name, String size, String uri) {
        return uri;
    }
}
```
