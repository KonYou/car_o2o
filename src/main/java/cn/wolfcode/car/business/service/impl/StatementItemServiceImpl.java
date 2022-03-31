package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.apache.catalina.core.ApplicationMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementItemServiceImpl implements IStatementItemService {

    @Autowired
    private StatementItemMapper statementItemMapper;

    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<StatementItem>(statementItemMapper.selectForList(qo));
    }


    @Override
    public void save(StatementItem statementItem) {
        statementItemMapper.insert(statementItem);
    }

    @Override
    public StatementItem get(Long id) {
        return statementItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(StatementItem statementItem) {
        statementItemMapper.updateByPrimaryKey(statementItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            statementItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    //TODO: 难啊难
    /** 模拟支付 */
    @Override
    public void payStatement(Long statementId) {
        //确认支付-支付成功
        Statement statement = statementMapper.selectByPrimaryKey(statementId);
        //结算单-收款时间，收款人，结算单状态：已支付
        statement.setPayTime(new Date());
        statement.setPayeeId(ShiroUtils.getUserId());
        statement.setStatus(Statement.STATUS_PAID);
        statementMapper.updatePay(statement);

        //如果是预约用户，修改预约状态为结算单生成
        if (statement.getAppointmentId() != null){
            appointmentMapper.changeStatus(statement.getAppointmentId(), Appointment.STATUS_SETTLE);
        }

    }

    //TODO: 难啊，难
    @Override
    public void saveItems(List<StatementItem> items) {
        if (items != null){
            //排除掉最后一个 因为最后一个与结算单无关
            StatementItem si = items.remove(items.size() - 1);
            //保存之前先将之前的数据删掉
            statementItemMapper.deleteByPrimaryKey(si.getStatementId());
            BigDecimal totalCount = new BigDecimal("0");
            BigDecimal totalAmount = new BigDecimal("0.00");
            //保存结算单明细
            if (items.size() > 0){
                //最后那个优惠价格
                //结算单明细
                for (StatementItem item :items){
                    //总数：所有item的quantity 的和
                    totalCount = totalCount.add(new BigDecimal(item.getItemQuantity()));
                    //总价格：每一个item的（itemPrice * itemQuantity） 和
                    totalAmount = totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getItemQuantity())));
                    //totalAmount = totalAmount
                    statementItemMapper.insert(item);
                }
                //修改结算单总价格 优惠价格 总数
                //优惠价格  si.getItemPrice();
                //总数：所有item的quantity 的和
                //总价格：每一个item的（itemPrice * itemQuantity）的和
                statementMapper.updateAmount(si.getStatementId(),si.getItemPrice(),totalCount,totalAmount);
            }else {
                //当结算单中明细全删了需要对结算中统计数进行复位
                statementMapper.updateAmount(si.getStatementId(),new BigDecimal("0.00"),totalCount,totalAmount);
            }

        }
    }

    @Override
    public List<StatementItem> list() {
        return statementItemMapper.selectAll();
    }
}
