package com.toolkit.scantaskmng.controller;

import com.toolkit.scantaskmng.service.NodesManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/nodes/manage")
public class TestManage {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NodesManageService nodesManageService;

    public TestManage(NodesManageService nodesManageService) {
        this.nodesManageService = nodesManageService;
    }

    // 测试命令：  http://localhost:8191/nodes/manage/run-task?uuid=0022f9d3-a2b6-4e73-b128-8b883c76bdf3
    @RequestMapping(value = "/run-task", method = RequestMethod.GET)
    @ResponseBody
    public Object runTask(@RequestParam("uuid") String taskUuid) {
        return nodesManageService.runTask(taskUuid);
    }
}