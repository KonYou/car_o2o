package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 审核流程接口
 */
public interface IBpmnInfoService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    BpmnInfo get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(BpmnInfo post);

  
    /**
     * 更新
     * @param post
     */
    void update(BpmnInfo post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<BpmnInfo> list();

    String upload(MultipartFile file);

    void deploy(String bpmnPath, String bpmnType, String info);

    void updateInfo(BpmnInfo bpmnInfo);

    void delete(Long id);

    InputStream readResource(String deploymentId, String type);

    /** 通过类型查找流程定义对象 */
    public BpmnInfo queryByType(String type);
}
