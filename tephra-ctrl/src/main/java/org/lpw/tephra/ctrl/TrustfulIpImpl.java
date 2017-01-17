package org.lpw.tephra.ctrl;

import org.lpw.tephra.storage.StorageListener;
import org.lpw.tephra.storage.Storages;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.trustful-ip")
public class TrustfulIpImpl implements TrustfulIp, StorageListener {
    @Inject
    private Converter converter;
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${tephra.ctrl.trustful-ip:/WEB-INF/trustful-ip}")
    private String trustfulIp;
    private Set<String> ips = new HashSet<>();
    private Set<String> patterns = new HashSet<>();

    @Override
    public boolean contains(String ip) {
        if (ips.contains(ip))
            return true;

        for (String pattern : patterns)
            if (validator.isMatchRegex(pattern, ip))
                return true;

        return false;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{trustfulIp};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        Set<String> ips = new HashSet<>();
        Set<String> patterns = new HashSet<>();
        for (String string : converter.toArray(new String(io.read(absolutePath)), "\n")) {
            string = string.trim();
            if (string.equals("") || string.startsWith("#"))
                continue;

            if (string.startsWith("rg"))
                patterns.add(string.substring(2));
            else
                ips.add(string);
        }
        this.ips = ips;
        this.patterns = patterns;

        if (logger.isInfoEnable())
            logger.info("更新信任IP[{}|{}]集。", converter.toString(ips), converter.toString(patterns));
    }
}
