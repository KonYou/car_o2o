package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

public interface IBusServiceItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BusServiceItem> query(BusServiceItemQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    BusServiceItem get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(BusServiceItem post);


    /**
     * 更新
     * @param post
     */
    void update(BusServiceItem post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<BusServiceItem> list();

    void saveEdit(BusServiceItem serviceItem);
}
