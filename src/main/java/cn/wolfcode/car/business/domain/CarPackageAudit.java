package cn.wolfcode.car.business.domain;

import cn.wolfcode.car.base.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 套餐审核对象
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CarPackageAudit {
    public static final Integer STATUS_IN_ROGRESS = 0;//审核中
    public static final Integer STATUS_REJECT = 1;//审核拒绝
    public static final Integer STATUS_PASS = 2;//审核通过
    public static final Integer STATUS_CANCEL = 3;//审核撤销
    private static final long serialVersionUID = 1L;


    /** 主键*/
    private Long id;

    /** 服务单项id*/
    private Long serviceItemId;

    /** 服务单项备注*/
    private String serviceItemInfo;

    /** 服务单项审核价格*/
    private BigDecimal serviceItemPrice;

    /** 关联服务单项对象 */
    private BusServiceItem serviceItem;

    /** 流程实例id*/
    private String instanceId;

    /** 创建者*/
    private String creator;

    /** 当前审核人id*/
    private Long auditorId;

    /**当前审核人对象*/
    private User auditor;

    /** 关联流程id*/
    private Long bpmnInfoId;
    /** 关联流程定义对象 */
    private BpmnInfo bpmnInfo;

    /** 备注*/
    private String info;

    /** 状态【进行中0/审核拒绝1/审核通过2/审核撤销3】*/
    private Integer status  = STATUS_IN_ROGRESS;

    /** 审核时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss", timezone = "GMT+8")
    private Date auditTime;

    /** 创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss", timezone = "GMT+8")
    private Date createTime;

    /** 审核记录信息*/
    private String auditRecord;
}