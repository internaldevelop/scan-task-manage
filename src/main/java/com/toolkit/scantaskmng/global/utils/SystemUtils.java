package com.toolkit.scantaskmng.global.utils;

import com.alibaba.fastjson.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

public class SystemUtils {
    public static Properties sysProps = System.getProperties();

    public static InetAddress getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public String getOsName() {
        return sysProps.getProperty("os.name");
    }

    static public String getOsArch() {
        return sysProps.getProperty("os.arch");
    }

    static public String getOsVersion() {
        return sysProps.getProperty("os.version");
    }

    static public boolean isWindows() {
        String osName = getOsName();
        return osName.indexOf("Windows") >= 0;
    }

    static public String getProp(String propName) {
        return sysProps.getProperty(propName);
    }

    static public JSONObject getProps() {
        String[] keys = {
                "os.name", "sun.boot.library.path", "user.dir", "user.country", "java.runtime.version",
                "os.arch", "line.separator", "os.version", "user.home", "user.timezone", "user.name",
                "user.language", "file.separator",
                "java.specification.version", "java.home", "sun.arch.data.model", "awt.toolkit",
                "sun.jnu.encoding", "java.vm.version", "java.library.path", "java.class.version",
                "java.runtime.name", "java.vm.vendor", "file.encoding", "java.version", "java.vendor",
                "java.vm.name", "sun.os.patch.level", "PID", "catalina.base", "sun.cpu.endian",
                "java.awt.graphicsenv", "java.endorsed.dirs", "java.io.tmpdir", "sun.desktop"
        };
        JSONObject props = new JSONObject();

        for (String key: keys) {
            props.put(key, sysProps.getProperty(key));
        }
        return props;
    }

}
