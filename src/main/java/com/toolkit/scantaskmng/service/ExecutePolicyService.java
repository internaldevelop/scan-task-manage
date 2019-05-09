package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONArray;
import com.toolkit.scantaskmng.bean.po.PolicyPo;
import com.toolkit.scantaskmng.bean.po.TaskExecuteResultsPo;
import com.toolkit.scantaskmng.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.enumeration.PolicyRunModeEnum;
import com.toolkit.scantaskmng.global.enumeration.RunStatusEnum;
import com.toolkit.scantaskmng.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

@Component
public class ExecutePolicyService {
    private final TaskExecuteResultsMapper taskExecuteResultsMapper;

    public ExecutePolicyService(TaskExecuteResultsMapper taskExecuteResultsMapper) {
        this.taskExecuteResultsMapper = taskExecuteResultsMapper;
    }

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

    private String initPolicyExecRecord(String taskUuid, PolicyPo policyPo) {

        TaskExecuteResultsPo resultPo = new TaskExecuteResultsPo();

        String resultUuid = MyUtils.generateUuid();
        resultPo.setUuid(resultUuid);

        resultPo.setTask_uuid(taskUuid);
        resultPo.setPolicy_uuid(policyPo.getUuid());

        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        resultPo.setStart_time(currentTime);
        resultPo.setCreate_time(currentTime);
        resultPo.setProcess_flag(RunStatusEnum.RUNNING.getStatus());
        int rv = taskExecuteResultsMapper.addExecuteRecord(resultPo);
        if (rv < 1)
            return "";

        return resultUuid;
    }

    private ErrorCodeEnum analyzeResult(String resultUuid, String result) {
        TaskExecuteResultsPo resultPo = new TaskExecuteResultsPo();

        resultPo.setUuid(resultUuid);
        resultPo.setRisk_flag(0);
        resultPo.setRisk_desc("Some risks");
        resultPo.setProcess_flag(RunStatusEnum.COMPLETE.getStatus());
        resultPo.setEnd_time(MyUtils.getCurrentSystemTimestamp());
        resultPo.setResults(result);
        int rv = taskExecuteResultsMapper.updateExecResult(resultPo);
        if (rv < 1)
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;

        return ErrorCodeEnum.ERROR_OK;
    }

    public ErrorCodeEnum executePolicy(String taskUuid, PolicyPo policyPo) {
        // 启动任务前创建记录
        String resultUuid = initPolicyExecRecord(taskUuid, policyPo);
        if (resultUuid.length() == 0) {
            return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
        }

        if (policyPo.getRun_mode() == PolicyRunModeEnum.COMMAND.getMode()) {
            // 执行系统命令或工具命令
            Runtime runtime = Runtime.getRuntime();
            try {
                Process proc = runtime.exec("cmd.exe /c " + policyPo.getRun_contents());
                BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));

                String line = null;

                String results = "";
                while ((line = input.readLine()) != null) {
                    results += line + "\n";
                }

                int exitVal = proc.waitFor();
                System.out.println("Exited with error code " + exitVal);
                if (analyzeResult(resultUuid, results) != ErrorCodeEnum.ERROR_OK)
                    return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
            } catch (IOException e) {
                e.printStackTrace();
                return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
            }

        }
        return ErrorCodeEnum.ERROR_OK;
    }

}
