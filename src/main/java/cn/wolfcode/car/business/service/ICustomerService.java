package cn.wolfcode.car.business.service;

import cn.wolfcode.car.base.query.CustomerQuery;
import cn.wolfcode.car.business.domain.Customer;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface ICustomerService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Customer> query(CustomerQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Customer get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(Customer post);

  
    /**
     * 更新
     * @param post
     */
    void update(Customer post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Customer> list();
}
