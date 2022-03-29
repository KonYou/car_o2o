package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementServiceImpl implements IStatementService {
    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    //修改
    @Override
    public void saveEdit(Statement statement) {
       
            Statement s = statementMapper.selectByPrimaryKey(statement.getId());

            if (Statement.STATUS_CONSUME.equals(s.getStatus())){
                statementMapper.statementModification(statement);
                return;
            }

            throw new BusinessException("消费中的客户才可以修改！");
        
    }

    @Override
    public TablePageInfo<Statement> query(StatementQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Statement>(statementMapper.selectForList(qo));
    }


    @Override
    public void save(Statement statement) {
        statement.setStatus(Statement.STATUS_CONSUME);
        statementMapper.insert(statement);
    }

    @Override
    public Statement get(Long id) {
        return statementMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Statement statement) {
        statementMapper.updateByPrimaryKey(statement);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            statementMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public Statement generateStatement(Long appointmentId) {
        //如果到店了===创建结算单 跳转到editDetail.html
        //如果结算单已经生成===创建结算单 跳转到editDetail.html/showDetail.html
        //存在了拿出来,没有就创建拿出来
        Appointment appointment = appointmentMapper.selectByPrimaryKey(appointmentId);
        Statement statement = new Statement();
        if (Appointment.STATUS_ARRIVAL.equals(appointment.getStatus())){
            //到店,创建结算单
            statement.setCustomerName(appointment.getCustomerName());
            statement.setCustomerPhone(appointment.getCustomerPhone());
            statement.setAppointmentId(appointmentId);
            statement.setActualArrivalTime(appointment.getActualArrivalTime());
            statement.setLicensePlate(appointment.getLicensePlate());
            statement.setCarSeries(appointment.getCarSeries());
            statement.setInfo(appointment.getInfo());
            statement.setServiceType(appointment.getServiceType()+0L);
            statement.setStatus(Statement.STATUS_CONSUME);

            statementMapper.generateInsert(statement);

            //修改预约状态:结算单生成
            appointmentMapper.changeStatus(appointmentId,Appointment.STATUS_SETTLE);
        }else {
            //查询结算单
            statement=statementMapper.queryByAppointmentId(appointmentId);
        }

        return statement;
    }

    @Override
    public List<Statement> list() {
        return statementMapper.selectAll();
    }
}
