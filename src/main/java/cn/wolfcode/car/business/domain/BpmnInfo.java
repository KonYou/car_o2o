package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BpmnInfo {
    /** 主键*/
    private Long id;

    /** 流程(图)名称*/
    private String bpmnName;

    /** 流程(图)类型*/
    private String bpmnType;

    /** 流程部署id*/
    private String deploymentId;

    /** activity流程定义生成的主键*/
    private String actProcessId;

    /** activity流程定义生成的key*/
    private String actProcessKey;

    /** 部署时间*/
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private Date deployTime;

    /** 描述信息*/
    private String info;

    /** 流程图 文件路径*/
    private String bpmnPath;
}