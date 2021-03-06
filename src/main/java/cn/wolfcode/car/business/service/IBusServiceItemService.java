package cn.wolfcode.car.business.service;

import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import org.apache.ibatis.annotations.Param;

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

    void takeDown(String id);

    void shelfOn(String id);

    void startAudit(Long id, Long bpmnInfoId,Long director, Long finance, String info);

    //修改审核状态
    void changeAuditStatus(Long id, Integer auditstatus);
}
