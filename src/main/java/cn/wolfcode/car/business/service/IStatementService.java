package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IStatementService {

    void saveEdit(Statement statement);

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Statement> query(StatementQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Statement get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(Statement post);

  
    /**
     * 更新
     * @param post
     */
    void update(Statement post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Statement> list();

    Statement generateStatement(Long appointmentId);
}
