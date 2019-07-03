package com.toolkit.scantaskmng.seconfig.linux;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

@Component
public class IptablesConfig {
    // iptables rules file:
    // Centos: /etc/sysconfig/iptables
    // UbuntuL: /etc/iptables.rules
    public JSONObject acquireIptablesProps() {
        JSONObject iptablesProps = new JSONObject();
        boolean active = MyUtils.checkServiceActive("iptables");
        iptablesProps.put("active", active);

        if (active) {
            if (!getOpenPorts(iptablesProps))
                return iptablesProps;
        }

        return iptablesProps;
    }

    // 查看开放的端口
    // iptalbles -nL
    private boolean getOpenPorts(JSONObject iptablesProps) {
        try {
            String[] args = new String[] { "iptables", "-nL" };
            BufferedReader output = MyUtils.getExecOutput(args);

            JSONArray portsArray = new JSONArray();
            String line;
            while ((line = output.readLine()) != null) {
                if (!line.startsWith("ACCEPT"))
                    continue;
                String[] params = line.split(" +");
                if (params.length >= 9 && params[6].equals("NEW") && params[8].contains(":")) {
                    JSONObject jsonPort = new JSONObject();
                    jsonPort.put("act", params[0]);
                    jsonPort.put("protocol", params[1]);
                    String[] port = params[8].split(":");
                    jsonPort.put("port", port[1]);

                    portsArray.add(jsonPort);
                }
            }

            iptablesProps.put("ports", portsArray);

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
