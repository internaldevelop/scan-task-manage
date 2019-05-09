package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.bean.po.PolicyPo;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.enumeration.PolicyRunModeEnum;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

@Component
public class ExecutePolicyService {
    public ErrorCodeEnum batchExecutePolicy(String taskUuid, JSONArray policyArray) {
        // 枚举每个策略
        for (Iterator iter = policyArray.iterator(); iter.hasNext(); ) {
            PolicyPo policyPo = (PolicyPo) iter.next();
//            PolicyPo policyPo = jsonPolicy.toJavaObject(PolicyPo.class);

            // 执行策略，如果执行失败，则中断返回
            if (executePolicy(taskUuid, policyPo) != ErrorCodeEnum.ERROR_OK) {
                return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
            }
        }
        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum executePolicy(String taskUuid, PolicyPo policyPo) {
        if (policyPo.getRun_mode() == PolicyRunModeEnum.COMMAND.getMode()) {
            // 执行系统命令或工具命令
            Runtime runtime = Runtime.getRuntime();
            try {
                Process proc = runtime.exec("cmd.exe /c " + policyPo.getRun_contents());
                BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));

                String line = null;

                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }

                int exitVal = proc.waitFor();
                System.out.println("Exited with error code " + exitVal);
            } catch (IOException e) {
                e.printStackTrace();
                return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return ErrorCodeEnum.ERROR_OK;
    }

}
