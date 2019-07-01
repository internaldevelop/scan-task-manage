package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.global.bean.ResponseBean;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.response.ResponseHelper;
import com.toolkit.scantaskmng.global.utils.SystemUtils;
import com.toolkit.scantaskmng.seconfig.linux.StartupSecConfig;
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

        if (all || typeList.contains("startup")) {
            jsonResp.put("SELinux", startupSecConfig.acquireSeLinuxInfo());

            jsonResp.put("selfRunServices", startupSecConfig.acquireSelfRunServices());
        }

        return responseHelper.success(jsonResp);
    }
}
