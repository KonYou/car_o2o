package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.mapper.BusServiceItemMapper;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.IBusServiceItemService;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.StringUtils;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BusServiceItemServiceImpl implements IBusServiceItemService {
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;

    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    @Override
    public TablePageInfo<BusServiceItem> query(BusServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BusServiceItem>(busServiceItemMapper.selectForList(qo));
    }


    @Override
    public void save(BusServiceItem busServiceItem) {
        busServiceItem.setCreateTime(new Date());
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())) {
            busServiceItem.setAuditStatus(BusServiceItem.AUDITSTATUS_INIT);
        }
        busServiceItemMapper.insert(busServiceItem);
    }

    @Override
    public BusServiceItem get(Long id) {
        return busServiceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(BusServiceItem busServiceItem) {
        busServiceItemMapper.updateByPrimaryKey(busServiceItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            busServiceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<BusServiceItem> list() {
        return busServiceItemMapper.selectAll();
    }

    @Override
    /** 修改操作 */
    public void saveEdit(BusServiceItem serviceItem) {
        //修改操作需要满足以下条件方可修改  1、上架状态不可修改  2、审核中的不可修改

        BusServiceItem busServiceItem = busServiceItemMapper.selectByPrimaryKey(serviceItem.getId());
        //上架状态不可修改
        if (BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())) {
            throw new BusinessException("上架状态不可修改！");
        }

        //是套餐且在审核中
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())
                && BusServiceItem.AUDITSTATUS_AUDITING.equals(busServiceItem.getAuditStatus())) {
            throw new BusinessException("审核中不可修改！");
        }

        busServiceItemMapper.saveByPrimaryKey(serviceItem);
    }

    /**
     * 下架操作
     */
    @Override
    public void takeDown(String id) {
        //如果id为空
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException("下架的id不能为空！");
        }

        //重复下架
        BusServiceItem busServiceItem = busServiceItemMapper.selectByPrimaryKey(Long.valueOf(id));
        if (BusServiceItem.SALESTATUS_OFF.equals(busServiceItem.getSaleStatus())) {
            throw new BusinessException("你已经下架过了，不能重复下架！");
        }

        //下架
        int valueOf = Integer.parseInt(id);
        busServiceItemMapper.takeDown(valueOf);
    }

    @Override
    public void changeAuditStatus(Long id, Integer auditstatus) {
        busServiceItemMapper.changeAuditStatus(id,auditstatus);
    }

    //审核
    @Override
    public void startAudit(Long id, Long bpmnInfoId,Long director, Long finance, String info) {
        BusServiceItem serviceItem = busServiceItemMapper.selectByPrimaryKey(id);
        if (serviceItem == null
                || BusServiceItem.CARPACKAGE_NO.equals(serviceItem.getCarPackage())
                || !BusServiceItem.AUDITSTATUS_INIT.equals(serviceItem.getAuditStatus())) {
            throw new BusinessException("参数异常！");
        }

        //保存套餐审核记录对象---业务线
        CarPackageAudit audit = new CarPackageAudit();

        if (director == null){
            throw new BusinessException("必须指定第一审核人！");
        }
        audit.setAuditorId(director);
        audit.setBpmnInfoId(bpmnInfoId);
        audit.setCreateTime(new Date());
        audit.setCreator(ShiroUtils.getLoginName());
        audit.setInfo(info);
        audit.setServiceItemId(id);
        audit.setServiceItemInfo(serviceItem.getInfo());
        audit.setServiceItemPrice(serviceItem.getDiscountPrice());
        audit.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        carPackageAuditService.save(audit);

        //修改养修服务套餐单项状态为审核中
        busServiceItemMapper.changeAuditStatus(id,BusServiceItem.AUDITSTATUS_AUDITING);
        //启动审核流程
        //--设置审核参数--审核人，审核价格
        Map<String,Object> map = new HashedMap();
        map.put("director",director);
        if (finance != null){
            map.put("finance",finance);
        }
        map.put("discountPrice",serviceItem.getDiscountPrice().longValue());

        //设置businessKey
        String businessKey = audit.getId().toString();
        BpmnInfo bpmnInfo = bpmnInfoService.get(bpmnInfoId);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), businessKey, map);

        //更新启动审核流程对象id 到套餐审核记录表中
        carPackageAuditService.updateInstanceId(audit.getId(),instance.getId());

        audit.setInstanceId(instance.getProcessInstanceId());
    }

    /**
     * 上架操作
     */
    @Override
    public void shelfOn(String id) {
        //如果id为空
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException("上架的id不能为空！");
        }

        BusServiceItem busServiceItem = busServiceItemMapper.selectByPrimaryKey(Long.valueOf(id));
        //如果是套餐且未通过审核
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())
                && !(BusServiceItem.AUDITSTATUS_APPROVED.equals(busServiceItem.getAuditStatus()))) {
            throw new BusinessException("需审核通过方可上架此商品！");
        }

        //重复上架
        if (BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())) {
            throw new BusinessException("你已经上架过了，不能重复上架！");
        }

        //上架
        int valueOf = Integer.parseInt(id);
        busServiceItemMapper.shelfOn(valueOf);
    }
}
