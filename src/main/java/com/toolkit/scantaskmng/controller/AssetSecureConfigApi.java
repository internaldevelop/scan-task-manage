package com.toolkit.scantaskmng.controller;

import com.toolkit.scantaskmng.global.response.ResponseHelper;
import com.toolkit.scantaskmng.service.AssetSecurityConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping(value="asset-sec-cfg")
public class AssetSecureConfigApi {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private AssetSecurityConfigService secConfigService;

    /**
     * 2.1 Get security config parameters of the asset. All or some partials.
     * @param types comma split string, example: "startup,system account,login,system log"
     * @return
     */
    @RequestMapping(value="/acquire", method = RequestMethod.GET)
    @ResponseBody
    public Object getAssetSecurityConfig(@RequestParam("types") String types) {
        return secConfigService.fetchSecurityConfig(types);
    }
}
