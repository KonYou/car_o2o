package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StatementMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Statement record);

    Statement selectByPrimaryKey(Long id);

    List<Statement> selectAll();

    int updateByPrimaryKey(Statement record);

    List<Statement> selectForList(StatementQuery qo);

    void statementModification(Statement statement);

    void updateAmount(@Param("statementId") Long statementId, @Param("price") BigDecimal itemPrice, @Param("totalQuantity") BigDecimal totalCount, @Param("totalAmount") BigDecimal totalAmount);

    void updatePay(Statement statement);

    Statement queryByAppointmentId(Long appointmentId);

    void generateInsert(Statement statement);
}