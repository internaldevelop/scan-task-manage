package com.toolkit.scantaskmng.controller;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.global.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/test")
public class TestController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/os", method = RequestMethod.GET)
    @ResponseBody
    public Object getOsInfo() {
        JSONObject jsonOS = new JSONObject();
        jsonOS.put("OS Name", SystemUtils.getOsName());
        jsonOS.put("OS Arch", SystemUtils.getOsArch());
        jsonOS.put("OS Version", SystemUtils.getOsVersion());
        return jsonOS;
    }
}
