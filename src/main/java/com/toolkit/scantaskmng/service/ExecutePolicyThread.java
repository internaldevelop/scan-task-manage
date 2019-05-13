package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONArray;
import com.toolkit.scantaskmng.bean.po.PolicyPo;
import com.toolkit.scantaskmng.bean.po.TaskExecuteResultsPo;
import com.toolkit.scantaskmng.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.enumeration.PolicyRunModeEnum;
import com.toolkit.scantaskmng.global.enumeration.RiskLevelEnum;
import com.toolkit.scantaskmng.global.enumeration.RunStatusEnum;
import com.toolkit.scantaskmng.global.utils.MyFileUtils;
import com.toolkit.scantaskmng.global.utils.MyUtils;
import com.toolkit.scantaskmng.global.utils.SpringBeanUtil;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ExecutePolicyThread implements Runnable{
    private String taskUuid;
    private JSONArray policyArray;

    private TaskExecuteResultsMapper taskExecuteResultsMapper = null;

    public ExecutePolicyThread() {
        taskExecuteResultsMapper = SpringBeanUtil.getBean(TaskExecuteResultsMapper.class);
//                ApplicationContext applicationContext;
//        applicationContext = new AnnotationConfigApplicationContext(com.xiaoleitech.authapi.AuthapiApplication.class);
//        SystemGlobalParams systemGlobalParams = applicationContext.getBean(SystemGlobalParams.class);

    }

    public void setParams(String taskUuid, JSONArray policyArray) {
        this.taskUuid = taskUuid;
        this.policyArray = policyArray;
    }

    @Override
    public void run() {
        batchExecutePolicy(this.taskUuid, this.policyArray);
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

    private ErrorCodeEnum saveResult(String resultUuid, RiskLevelEnum riskLevel, String riskDesc, String result) {
//        MyFileUtils.save(result,"temp.txt", false);
        TaskExecuteResultsPo resultPo = new TaskExecuteResultsPo();

        resultPo.setUuid(resultUuid);
        resultPo.setRisk_flag(riskLevel.getLevel());
        resultPo.setRisk_desc(riskDesc);
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

        ErrorCodeEnum errorCode;
        if (policyPo.getRun_mode() == PolicyRunModeEnum.PYTHON_SCRIPTS.getMode()) {
            // 执行 python 脚本
            errorCode = execPython(resultUuid, policyPo.getRun_contents());
            if (errorCode != ErrorCodeEnum.ERROR_OK)
                return errorCode;
        } else if (policyPo.getRun_mode() == PolicyRunModeEnum.COMMAND.getMode()) {
            // 执行系统命令或工具命令
            // 由于解析的难度，本版本不支持
//            errorCode = execCommand(resultUuid, policyPo.getRun_contents());
//            if (errorCode != ErrorCodeEnum.ERROR_OK)
//                return errorCode;
        }

        return ErrorCodeEnum.ERROR_OK;
    }

    private ErrorCodeEnum execPython(String resultUuid, String pyScript) {
        // 保存 python 内容为临时文件
        MyFileUtils.save(pyScript, "tempexec.py");

        String[] args1 = new String[]{"python","tempexec.py"};
        try {
            Process proc = Runtime.getRuntime().exec(args1);
            // 中文版 Windows 运行时环境的输出默认是 GBK 编码
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));
            String line;
            String results = "";
            while ((line = input.readLine()) != null) {
                results += line + "\n";
            }
            input.close();

            int exitVal = proc.waitFor();
            System.out.println("Exited with error code " + exitVal);
            if (saveResult(resultUuid, RiskLevelEnum.RISK_L1, "Risk Level-1", results) != ErrorCodeEnum.ERROR_OK)
                return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ErrorCodeEnum.ERROR_OK;
    }

    private ErrorCodeEnum execCommand(String resultUuid, String cmdInfo) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("cmd.exe /c " + cmdInfo);
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));

            String line;
            String results = "";
            while ((line = input.readLine()) != null) {
                results += line + "\n";
            }
            input.close();

            int exitVal = proc.waitFor();
            System.out.println("Exited with error code " + exitVal);
            if (saveResult(resultUuid, RiskLevelEnum.NO_RISK, "No Risk", results) != ErrorCodeEnum.ERROR_OK)
                return ErrorCodeEnum.ERROR_INTERNAL_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ErrorCodeEnum.ERROR_FAIL_EXEC_POLICY;
        }

        return ErrorCodeEnum.ERROR_OK;
    }


}
