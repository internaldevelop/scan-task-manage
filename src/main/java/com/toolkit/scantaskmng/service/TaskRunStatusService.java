package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.bean.dto.TaskRunStatusDto;
import com.toolkit.scantaskmng.global.redis.IRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskRunStatusService {
    @Autowired
    private IRedisClient redisClient;

    private String _getTaskRedisKey(String taskUuid) {
        return "task_run_" + taskUuid;
    }

    public TaskRunStatusDto getTaskRunStatus(String taskUuid) {
        String key = _getTaskRedisKey(taskUuid);
        String value = (String)redisClient.get(key);
        JSONObject jsonObject = JSONObject.parseObject(value);
        TaskRunStatusDto taskRunStatusDto = jsonObject.getObject("status", TaskRunStatusDto.class);
        return taskRunStatusDto;
    }
    public boolean setTaskRunStatus(String taskUuid, TaskRunStatusDto taskRunStatusDto) {
        String key = _getTaskRedisKey(taskUuid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", taskRunStatusDto);
        return redisClient.set(key, jsonObject.toJSONString());
    }

    public String getString(String taskUuid) {
        String key = _getTaskRedisKey(taskUuid);
        return (String) redisClient.get(key, 0);
    }

    public boolean setString(String taskUuid, String value) {
        String key = _getTaskRedisKey(taskUuid);
        return redisClient.set(key, value);
    }

}
