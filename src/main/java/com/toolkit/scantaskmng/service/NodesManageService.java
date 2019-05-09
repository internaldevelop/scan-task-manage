package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.bean.po.PolicyPo;
import com.toolkit.scantaskmng.bean.po.TaskPo;
import com.toolkit.scantaskmng.dao.mybatis.PoliciesMapper;
import com.toolkit.scantaskmng.dao.mybatis.TasksMapper;
import com.toolkit.scantaskmng.global.bean.ResponseBean;
import com.toolkit.scantaskmng.global.enumeration.ErrorCodeEnum;
import com.toolkit.scantaskmng.global.response.ResponseHelper;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class NodesManageService {
    private final ResponseHelper responseHelper;
    private final TasksMapper tasksMapper;
    private final PoliciesMapper policiesMapper;
    private final ExecutePolicyService executePolicyService;

    public NodesManageService(ResponseHelper responseHelper, TasksMapper tasksMapper, PoliciesMapper policiesMapper, ExecutePolicyService executePolicyService) {
        this.responseHelper = responseHelper;
        this.tasksMapper = tasksMapper;
        this.policiesMapper = policiesMapper;
        this.executePolicyService = executePolicyService;
    }

    public ResponseBean runTask(String taskUuid) {
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

        // 执行策略
        ErrorCodeEnum errorCode = executePolicyService.batchExecutePolicy(taskUuid, policyArray);
        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            return responseHelper.error(errorCode, policyArray);
        } else {
            return responseHelper.success(policyArray);
        }

    }
}
