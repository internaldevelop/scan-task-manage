package com.toolkit.scantaskmng.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.scantaskmng.bean.dto.TaskRunStatusDto;
import com.toolkit.scantaskmng.global.redis.IRedisClient;
import com.toolkit.scantaskmng.service.mq.TopicSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskRunStatusService {
    @Autowired
    private IRedisClient redisClient;

    @Autowired
    private TopicSender topicSender;

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
        // 在redis缓存中记录任务运行状态
        String key = _getTaskRedisKey(taskUuid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", taskRunStatusDto);
        if  (!redisClient.set(key, jsonObject.toJSONString()))
            return false;

        // 发送MQ消息
        topicSender.sendRunStatusTopic(jsonObject.toJSONString());
        return true;
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
