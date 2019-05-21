package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.bean.dto.TaskRunStatusDto;
import com.toolkit.scantaskmng.bean.po.PolicyPo;
import com.toolkit.scantaskmng.bean.po.TaskPo;
import com.toolkit.scantaskmng.dao.mybatis.PoliciesMapper;
import com.toolkit.scantaskmng.dao.mybatis.TasksMapper;
import com.toolkit.scantaskmng.global.bean.ResponseBean;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.enumeration.TaskRunStatusEnum;
import com.toolkit.scantaskmng.global.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class NodesManageService {
    @Autowired
    private ResponseHelper responseHelper;
    @Autowired
    private TasksMapper tasksMapper;
    @Autowired
    private PoliciesMapper policiesMapper;
    @Autowired
    private TaskRunStatusService taskRunStatusService;

    @Autowired
    private ExecutePolicyThread execThread;

    public NodesManageService() {
//        if (this.execThread2 == null)
//            this.execThread2 = new ExecutePolicyThread();
    }

    public ResponseBean runTask(String taskUuid) {
        // 执行策略
//        ExecutePolicyThread execThread = new ExecutePolicyThread();
//        execThread.setParams(taskUuid);
        TaskRunQueue.add(taskUuid);
        Thread thread = new Thread(execThread);
        thread.start();

//        String value = taskRunStatusService.getString("AAAA");
//        taskRunStatusService.setString("AAAA", "A1111");
//        value = taskRunStatusService.getString("AAAA");
/*
        // 获取任务执行状态
        TaskRunStatusDto taskRunStatusDto = taskRunStatusService.getTaskRunStatus(taskUuid);
        if (taskRunStatusDto == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_RUN_STATUS_NOT_FOUND);

        // TODO: 没有必要要求任务运行状态为空闲
//        if (taskRunStatusDto.getRun_status() != TaskRunStatusEnum.IDLE.getStatus())
//            return responseHelper.error(ErrorCodeEnum.ERROR_INCORRECT_TASK_RUN_STATUS);

        // 获取指定的任务
        TaskPo taskPo = tasksMapper.getTaskByUuid(taskUuid);
        if (taskPo == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        // 提取策略组
        JSONArray policyGroups = JSONArray.parseArray(taskPo.getPolicy_groups());

        // 构造策略集合
        JSONArray policyArray = new JSONArray();
        for ( Iterator iter = policyGroups.iterator(); iter.hasNext(); ) {
            JSONObject jsonGroup = (JSONObject)iter.next();
            String policyGroupUuid = jsonGroup.getString("uuid");
            List<PolicyPo> policyPoList = policiesMapper.getPoliciesByGroup(policyGroupUuid);
            if (policyPoList == null || policyPoList.size() == 0) {
                // 如果找不到该组的策略，继续寻找下一个组
                continue;
//                return responseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND, policyGroups);
            }

            // 找到的策略加到该分组对象中
            policyArray.addAll(policyPoList);
        }

//        ErrorCodeEnum errorCode = executePolicyThread.batchExecutePolicy(taskUuid, policyArray);
//        if (errorCode != ErrorCodeEnum.ERROR_OK) {
//            return responseHelper.error(errorCode, policyArray);
//        } else {
//            return responseHelper.success(policyArray);
//        }
*/
        return responseHelper.success();

    }
}
