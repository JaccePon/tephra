package org.lpw.tephra.test;

import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class MockResponseImpl implements MockResponse {
    private String contentType;
    private OutputStream outputStream;
    private String url;

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public String getRedirectTo() {
        return url;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();

        return outputStream;
    }

    @Override
    public void redirectTo(String url) {
        this.url = url;
    }

    @Override
    public void sendError(int code) {
    }

    @Override
    public JSONObject asJson() {
        return JSONObject.fromObject(getOutputStream().toString());
    }
}
