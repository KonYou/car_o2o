package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.mapper.BusServiceItemMapper;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.business.service.IBusServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.util.Convert;
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
    public void saveEdit(BusServiceItem serviceItem) {
        busServiceItemMapper.saveByPrimaryKey(serviceItem);
    }
}
