package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.file.FileUploadUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

@Service
@Transactional
public class BpmnInfoServiceImpl implements IBpmnInfoService {

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BpmnInfo>(bpmnInfoMapper.selectForList(qo));
    }


    @Override
    public void save(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public BpmnInfo get(Long id) {
        return bpmnInfoMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.updateByPrimaryKey(bpmnInfo);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            bpmnInfoMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public InputStream readResource(String deploymentId, String type) {
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .latestVersion()
                .singleResult();
        if ("xml".equalsIgnoreCase(type)){
            //xml
            return repositoryService.getResourceAsStream(deploymentId,processDefinition.getResourceName());
        }else if ("png".equalsIgnoreCase(type)){
            //png
            BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());
            DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            //generateDiagram(流程模型，需要高亮的节点，需要高亮的线条，后面三个参数都表示是字体)
            return generator.generateDiagram(model, Collections.EMPTY_LIST,Collections.EMPTY_LIST,"宋体","宋体","宋体");
        }
        return null;
    }

    /** 删除 */
    @Override
    public void delete(Long id) {
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(id);
        //1、删除流程部署对象记录，流程定义数据
        //参数1：部署对象id，参数2：是否级联删除
        //流程部署成功之后，就可以根据流程定义发起流程实例审核（允许多个）
        //如果此时删除流程定义/部署对象信息，需要使用级联删除
        repositoryService.deleteDeployment(bpmnInfo.getDeploymentId(),true);
        //2: 流程文件
        File file = new File(SystemConfig.getProfile(),bpmnInfo.getBpmnPath());
        if (file.exists()) {
            file.delete();
        }

        //3: 删除审核流程定义对象
        bpmnInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateInfo(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.updateInfoByPrimaryKey(bpmnInfo);
    }

    /** 获取流程文件然后部署 */
    @Override
    public void deploy(String bpmnPath, String bpmnType, String info) {
        Deployment deployment = null;
        //获取流程文件然后部署
        try {
            FileInputStream stream = new FileInputStream(new File(SystemConfig.getProfile(),bpmnPath));
            String lowStr = bpmnPath.toLowerCase();//避免后缀大写的情况，全部转成小写

            if (lowStr.endsWith("zip")){
                //流程部署使用服务：repositoryService
                deployment = repositoryService.createDeployment()
                        .addZipInputStream(new ZipInputStream(stream))
                        .deploy();

            }else if (lowStr.endsWith("bpmn")){
                deployment = repositoryService.createDeployment()
                        .addInputStream(bpmnPath, stream)
                        .deploy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //保存一条流程定义信息
        BpmnInfo bpmnInfo = new BpmnInfo();
        bpmnInfo.setDeploymentId(deployment.getId());
        bpmnInfo.setDeployTime(deployment.getDeploymentTime());
        bpmnInfo.setBpmnType(bpmnType);
        bpmnInfo.setBpmnPath(bpmnPath);
        bpmnInfo.setInfo(info);

        //获取流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .latestVersion()
                .singleResult();

        bpmnInfo.setBpmnName(processDefinition.getName());
        bpmnInfo.setActProcessId(processDefinition.getId());
        bpmnInfo.setActProcessKey(processDefinition.getKey());
        bpmnInfoMapper.insert(bpmnInfo);
    }

    /**
     * 文件上传操作
     */
    @Override
    public String upload(MultipartFile file) {
        String upload = null;
        // 上传的文件名
        String filename = file.getOriginalFilename();
        //截取后缀名
        String exp = FilenameUtils.getExtension(filename);

        if (file != null && file.getSize() > 0) {
            if (!("zip".equals(exp) || "bpmn".equals(exp))) {
                throw new BusinessException("流程定义文件仅支持 bpmn 和 zip 格式！");
            }

            try {
                //上传成功之后，返回路径
                upload = FileUploadUtils.upload(SystemConfig.getUploadPath(), file);//SystemConfig.getUploadPath()系统配置的上传路径
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException(e.getMessage());
            }
        }else {
            throw new BusinessException("不允许上传空文件！");
        }
        return upload;
    }

    @Override
    public List<BpmnInfo> list() {
        return bpmnInfoMapper.selectAll();
    }

    /** 通过类型查找流程定义对象 */
    public BpmnInfo queryByType(String type) {
        return bpmnInfoMapper.selectByPrimaryType(type);
    }
}
