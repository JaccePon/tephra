package org.lpw.tephra.test;

import java.util.Map;

/**
 * @author lpw
 */
public class MockHeaderImpl implements MockHeader {
    private String ip;
    private Map<String, String> map;

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String get(String name) {
        return map == null ? null : map.get(name);
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }
}
