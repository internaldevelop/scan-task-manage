package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.global.bean.ResponseBean;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.response.ResponseHelper;
import com.toolkit.scantaskmng.global.utils.SystemUtils;
import com.toolkit.scantaskmng.seconfig.linux.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AssetSecurityConfigService {
    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private StartupSecConfig startupSecConfig;
    @Autowired
    private AccountSecConfig accountSecConfig;
    @Autowired
    private PasswordConfig passwordConfig;
    @Autowired
    private SysServiceConfig serviceConfig;
    @Autowired
    private FirewallConfig firewallConfig;
    @Autowired
    private IptablesConfig iptablesConfig;
    @Autowired
    private LoginSecureConfig loginSecureConfig;

    public ResponseBean fetchSecurityConfig(String types) {
        // 不支持Windows系统安全配置采集
        if (SystemUtils.isWindows())
            return responseHelper.error(ErrorCodeEnum.ERROR_WIN_NO_SEC_CONFIG);

        boolean all = false;
        List<String> typeList = null;
        if (types.isEmpty()) {
            // if types has not assigned, means all the security config are needed.
            all = true;
        } else {
            typeList = Arrays.asList(types.split(","));
        }

        JSONObject jsonResp = new JSONObject();

        // Startup security config
        if (all || typeList.contains("startup")) {
            jsonResp.put("SELinux", startupSecConfig.acquireSeLinuxInfo());
            jsonResp.put("selfRunServices", startupSecConfig.acquireSelfRunServices());
        }

        // Account security config
        if (all || typeList.contains("account")) {
            jsonResp.put("accounts", accountSecConfig.acquireAccountProps());
            jsonResp.put("groups", accountSecConfig.acquireGroupProps());
        }

        // Password security config
        if (all || typeList.contains("password")) {
            jsonResp.put("passwordProps", passwordConfig.acquirePasswordProps());
        }

        // System service config
        if (all || typeList.contains("service")) {
            jsonResp.put("sshConfig", serviceConfig.acquireSSHProps());
            jsonResp.put("firewall", firewallConfig.acquireFirewallProps());
            jsonResp.put("iptables", iptablesConfig.acquireIptablesProps());
        }

        // Login security config
        if (all || typeList.contains("login")) {
            jsonResp.put("login", loginSecureConfig.acquireLoginProps());
        }

        return responseHelper.success(jsonResp);
    }
}
