package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface AppointmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Appointment record);

    Appointment selectByPrimaryKey(Long id);

    List<Appointment> selectAll();

    int updateByPrimaryKey(Appointment record);

    List<Appointment> selectForList(AppointmentQuery qo);

    void appointmentModification(Appointment serviceItem);

    void cancelHandler(Long ids);

    void arrivalHandler(@Param("id") Long id, @Param("date") Date date);

    void softDelete(Long id);

    void changeStatus(@Param("appointmentId") Long appointmentId, @Param("statusSettle") Integer statusSettle);
}