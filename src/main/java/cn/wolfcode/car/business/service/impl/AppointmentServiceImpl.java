package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.StringUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {
    @Autowired
    private AppointmentMapper appointmentMapper;


    @Override
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }


    @Override
    public void save(Appointment appointment) {
        appointmentMapper.insert(appointment);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {
        appointmentMapper.updateByPrimaryKey(appointment);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            appointmentMapper.deleteByPrimaryKey(dictId);
        }
    }

    //软删除
    @Override
    public void softDelete(Long ids) {
        if (ids == null){
            throw new BusinessException("订单id不能为空！");
        }

        Appointment appointment = appointmentMapper.selectByPrimaryKey(ids);

        if (Appointment.STATUS_CANCEL.equals(appointment.getStatus())
            || Appointment.STATUS_OVERTIME.equals(appointment.getStatus())){
            appointmentMapper.softDelete(ids);
            return;
        }

        throw new BusinessException("只有取消的订单才能删除！");
    }

    //客户到店
    @Override
    public void arrivalHandler(Long id, Date date) {
        if (id == null){
            throw new BusinessException("订单id不能为空！");
        }

        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);

        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())){
            appointmentMapper.arrivalHandler(id,date);
            return;
        }

        throw new BusinessException("只有预约状态才能点击到店！");
    }

    //客户取消订单
    @Override
    public void cancelHandler(Long id) {
        if (id == null){
            throw new BusinessException("订单id不能为空！");
        }

        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);

        if (Appointment.STATUS_CANCEL.equals(appointment.getStatus())){
            throw new BusinessException("您已经取消过了！");
        }

        if (!(Appointment.STATUS_SETTLE.equals(appointment.getStatus()))){
            appointmentMapper.cancelHandler(id);
            return;
        }

        throw new BusinessException("结算状态不能取消订单！");
    }

    //预约的修改
    @Override
    public void saveEdit(Appointment serviceItem) {
        Appointment appointment = appointmentMapper.selectByPrimaryKey(serviceItem.getId());

        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())){
            appointmentMapper.appointmentModification(serviceItem);
            return;
        }

        throw new BusinessException("只有预约状态才能修改！");
    }

    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }
}
