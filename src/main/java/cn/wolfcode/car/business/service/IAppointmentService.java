package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.Date;
import java.util.List;

/**
 * 岗位服务接口
 */
public interface IAppointmentService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Appointment> query(AppointmentQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Appointment get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(Appointment post);

  
    /**
     * 更新
     * @param post
     */
    void update(Appointment post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Appointment> list();

    void saveEdit(Appointment serviceItem);

    void cancelHandler(Long id);

    void arrivalHandler(Long id, Date date);

    void softDelete(Long ids);
}
