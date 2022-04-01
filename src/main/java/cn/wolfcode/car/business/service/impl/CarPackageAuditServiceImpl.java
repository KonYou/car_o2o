package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.IBusServiceItemService;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IBpmnInfoService bpmnInfoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IBusServiceItemService serviceItemService;


    @Override
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<CarPackageAudit>(carPackageAuditMapper.selectForList(qo));
    }


    @Override
    public void save(CarPackageAudit CarPackageAudit) {
        carPackageAuditMapper.insert(CarPackageAudit);
    }

    @Override
    public CarPackageAudit get(Long id) {
        return carPackageAuditMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(CarPackageAudit CarPackageAudit) {
        carPackageAuditMapper.updateByPrimaryKey(CarPackageAudit);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            carPackageAuditMapper.deleteByPrimaryKey(dictId);
        }
    }


    /** 完成审核 */
    @Override
    public void audit(Long id, Integer auditStatus, String info) {
        //满足条件判断
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        if (audit == null || !CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())){
            throw new BusinessException("只有在审核中状态时才可以进行审核");
        }

        //不管审核通过与拒绝都要执行的操作
        //1： 审核记录对象变动---审核信息---审核时间
        audit.setAuditTime(new Date());
        String auditRecord = "";
        //约定：2 为同意
        if (auditStatus == 2){
            //审核通过
            auditRecord += "【" + ShiroUtils.getUser().getUserName() + "审核通过,审核备注：" + info + "】";
        }else {
            //审核拒绝
            auditRecord += "【" + ShiroUtils.getUser().getUserName() + "审核拒绝,审核备注：" + info + "】";
        }

        if (audit.getAuditRecord() != null){
            audit.setAuditRecord(audit.getAuditRecord() + ";" + auditRecord);
        }else {
            audit.setAuditRecord(auditRecord);
        }

        //2：流程往下走--task执行
        Task task = taskService.createTaskQuery().processInstanceId(audit.getInstanceId()).singleResult();

        //设置流程分支条件值： 审核通过还是审核拒绝
        taskService.setVariable(task.getId(),"auditStatus",auditStatus);
        //设置流程的备注
        taskService.addComment(task.getId(),task.getProcessInstanceId(),auditRecord);
        //执行task任务--推动流程执行
        taskService.complete(task.getId());

        //再次查询任务，如果有值说明还有一个审核员要审核
        Task nextTask = taskService.createTaskQuery().processInstanceId(audit.getInstanceId()).singleResult();
        if (auditStatus == 2){  //审核通过
            //判断是否有下一个节点（审核员）
            if (nextTask != null){//有
                //审核记录对象变动 --- 设置下一个审核人
                String assignee = nextTask.getAssignee();
                audit.setAuditorId(Long.parseLong(assignee));
            }else {//没有
                //审核记录对象状态-审核通过
                audit.setStatus(CarPackageAudit.STATUS_PASS);
                //养修服务套餐状态-审核通过
                serviceItemService.changeAuditStatus(audit.getServiceItemId(),BusServiceItem.AUDITSTATUS_APPROVED);
            }
        }else {     //审核拒绝      流程结束
            //审核记录对象状态-审核拒绝
            audit.setStatus(CarPackageAudit.STATUS_REJECT);
            //养修服务套餐状态-初始化
            serviceItemService.changeAuditStatus(audit.getServiceItemId(),BusServiceItem.AUDITSTATUS_INIT);
        }
        carPackageAuditMapper.updateByPrimaryKey(audit);
    }

    /**
     * 撤销
     * @param id
     */
    @Override
    public void cancelApply(Long id) {
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        //1：撤销满足条件
        if (audit == null || !CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())){
            throw new BusinessException("参数异常");
        }
        //2：审核记录对象状态-撤销状态
        audit.setStatus(CarPackageAudit.STATUS_CANCEL);
        carPackageAuditMapper.updateByPrimaryKey(audit);

        //3：养修服务套餐对象--初始化
        serviceItemService.changeAuditStatus(audit.getServiceItemId(), BusServiceItem.AUDITSTATUS_INIT);

        //4：删除审核流程实例
        runtimeService.deleteProcessInstance(audit.getInstanceId(),"流程被撤销了");
    }

    /**
     * 审核进度图
     * @param id
     * @return
     */
    @Override
    public InputStream processImg(Long id) {
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        BpmnInfo bpmnInfo = bpmnInfoService.get(audit.getBpmnInfoId());
        BpmnModel model = repositoryService.getBpmnModel(bpmnInfo.getActProcessId());
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        //审核中-流程还运行中
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())) {
            Task task = taskService.createTaskQuery()
                    .processInstanceId(audit.getInstanceId())
                    .singleResult();

            List<String> activeActivityIds = runtimeService.getActiveActivityIds(task.getExecutionId());

            return generator.generateDiagram(model, activeActivityIds, Collections.EMPTY_LIST,
                    "宋体", "宋体", "宋体");
        } else {
            //审核结束
            return generator.generateDiagram(model, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                    "宋体", "宋体", "宋体");
        }
    }

    @Override
    public void updateInstanceId(Long id, String instanceId) {
        carPackageAuditMapper.updateInstanceId(id, instanceId);
    }

    @Override
    public List<CarPackageAudit> list() {
        return carPackageAuditMapper.selectAll();
    }
}
