package com.toolkit.scantaskmng.controller;

import com.toolkit.scantaskmng.global.response.ResponseHelper;
import com.toolkit.scantaskmng.global.utils.MyUtils;
import com.toolkit.scantaskmng.global.utils.SystemUtils;
import com.toolkit.scantaskmng.service.AssetInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/asset-info")
public class AssetInfoApi {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResponseHelper responseHelper;
    @Autowired
    private AssetInfoService assetInfoService;

    /**
     * 1.1 Get asset's all infos or some designated infos
     * @param types comma split string, example: "CPU,Memory,CPU Usage"
     * @return
     */
    @RequestMapping(value="/acquire", method = RequestMethod.GET)
    @ResponseBody
    public Object getAssetInfo(@RequestParam("types") String types) {
        return assetInfoService.fetchAssetInfo(types);
    }

    /**
     * 1.2 Get system property
     * @param propName
     * @return
     */
    @RequestMapping(value = "/system-prop")
    @ResponseBody
    public Object getSystemProps(@RequestParam("prop") String propName) {
//        return MyUtils.getWorkingPath();
        return SystemUtils.getProp(propName);
    }
}
