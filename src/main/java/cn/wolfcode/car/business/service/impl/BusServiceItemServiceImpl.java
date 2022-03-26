package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.mapper.BusServiceItemMapper;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.business.service.IBusServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BusServiceItemServiceImpl implements IBusServiceItemService {
    @Autowired
    private BusServiceItemMapper busServiceItemMapper;


    @Override
    public TablePageInfo<BusServiceItem> query(BusServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BusServiceItem>(busServiceItemMapper.selectForList(qo));
    }


    @Override
    public void save(BusServiceItem busServiceItem) {
        busServiceItem.setCreateTime(new Date());
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())){
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
        if (BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())){
            throw new BusinessException("上架状态不可修改！");
        }

        //是套餐且在审核中
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())
                && BusServiceItem.AUDITSTATUS_AUDITING.equals(busServiceItem.getAuditStatus())){
            throw new BusinessException("审核中不可修改！");
        }

        busServiceItemMapper.saveByPrimaryKey(serviceItem);
    }

    /** 下架操作 */
    @Override
    public void takeDown(String id) {
        //如果id为空
        if (StringUtils.isEmpty(id)){
            throw new BusinessException("下架的id不能为空！");
        }

        //重复下架
        BusServiceItem busServiceItem = busServiceItemMapper.selectByPrimaryKey(Long.valueOf(id));
        if (BusServiceItem.SALESTATUS_OFF.equals(busServiceItem.getSaleStatus())){
            throw new BusinessException("你已经下架过了，不能重复下架！");
        }

        //下架
        int valueOf = Integer.parseInt(id);
        busServiceItemMapper.takeDown(valueOf);
    }

    /** 上架操作 */
    @Override
    public void shelfOn(String id) {
        //如果id为空
        if (StringUtils.isEmpty(id)){
            throw new BusinessException("上架的id不能为空！");
        }

        BusServiceItem busServiceItem = busServiceItemMapper.selectByPrimaryKey(Long.valueOf(id));
        //如果是套餐且未通过审核
        if (BusServiceItem.CARPACKAGE_YES.equals(busServiceItem.getCarPackage())
                && !(BusServiceItem.AUDITSTATUS_APPROVED.equals(busServiceItem.getAuditStatus()))) {
            throw new BusinessException("需审核通过方可上架此商品！");
        }

        //重复上架
        if(BusServiceItem.SALESTATUS_ON.equals(busServiceItem.getSaleStatus())){
            throw new BusinessException("你已经上架过了，不能重复上架！");
        }

        //上架
        int valueOf = Integer.parseInt(id);
        busServiceItemMapper.shelfOn(valueOf);
    }
}
