package com.toolkit.scantaskmng.service;

import java.util.ArrayList;
import java.util.List;

public class TaskRunQueue {
    static private List<String> queue = new ArrayList<>();
    synchronized static public void add(String taskUuid) {
        queue.add(taskUuid);
    }
    synchronized static public String fetch() {
        if (queue.size() > 0) {
            String taskUuid = queue.get(0);
            queue.remove(0);
            return taskUuid;
        } else {
            return null;
        }
    }
}
