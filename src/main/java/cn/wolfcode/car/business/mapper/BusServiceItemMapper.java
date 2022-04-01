package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.domain.Customer;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusServiceItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BusServiceItem record);

    BusServiceItem selectByPrimaryKey(Long id);

    List<BusServiceItem> selectAll();

    int updateByPrimaryKey(BusServiceItem record);

    List<BusServiceItem> selectForList(BusServiceItemQuery qo);

    int saveByPrimaryKey(BusServiceItem record);

    void takeDown(int valueOf);

    void shelfOn(int valueOf);

    void changeAuditStatus(@Param("id") Long id, @Param("auditstatus") Integer auditstatusAuditing);
}