package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusServiceItemQuery extends QueryObject {
    /** 服务项名称*/
    private String name;
    /** 是否套餐【是/否】*/
    private Integer carPackage;
    /** 服务分类【维修/保养/其他】*/
    private Integer serviceCatalog;
    /** 审核状态【初始化/审核中/审核通过/审核拒绝/无需审核】*/
    private Integer auditStatus;
    /** 上架状态【已上架/未上架】*/
    private Integer saleStatus;
}
