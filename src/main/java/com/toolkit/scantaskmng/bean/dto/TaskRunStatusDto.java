package com.toolkit.scantaskmng.bean.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class TaskRunStatusDto implements Serializable {
    String task_uuid;           // 任务UUID
    int run_status;            // 任务状态
    int total_jobs_count;       // 要执行的子任务总数量
    int done_jobs_count;        // 已完成的子任务数量
    int remain_time;            // 预计剩余执行时间（单位：ms）
    int total_time;             // 预计总计执行时间（单位：ms）
    double   done_rate;          // 完成比例（单位：%，按执行时间计算）
}