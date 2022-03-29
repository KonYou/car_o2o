package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 *
 */
public interface IStatementItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<StatementItem> query(StatementItemQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    StatementItem get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(StatementItem post);


    /**
     * 更新
     * @param post
     */
    void update(StatementItem post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<StatementItem> list();

    /** 保存 */
    void saveItems(List<StatementItem> statementItems);

    void payStatement(Long statementId);
}
