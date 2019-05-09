package com.toolkit.scantaskmng.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * TaskExecuteResultsProps 对应数据库中task_execute_results表，
 * 任务结果
 */
@Component
@Data
public class TaskExecuteResultsPo {
    private int id;
    private String uuid;  // 任务执行结果的 UUID
    private String task_uuid;  // 任务的 UUID
    private String code;  // 任务代号
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp start_time;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp end_time;
    private String creator_uuid;  // 启动任务执行的用户 UUID
    private String results;  // 任务执行的结果
    private String process_flag;  // 任务执行过程的标识

}
