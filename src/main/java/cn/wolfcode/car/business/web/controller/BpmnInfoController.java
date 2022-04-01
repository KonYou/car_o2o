package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 审核流程控制器
 */
@Controller
@RequestMapping("business/bpmnInfo")
public class BpmnInfoController {
    //模板前缀
    private static final String prefix = "business/system/bpmnInfo/";

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:bpmnInfo:listPage")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:bpmnInfo:addPage")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:bpmnInfo:editPage")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("bpmnInfo", bpmnInfoService.get(id));
        return prefix + "edit";
    }

@RequiresPermissions("system:bpmnInfo:deployPage")
    @RequestMapping("/deployPage")
    public String deployPage(){
        return prefix + "deploy";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:bpmnInfo:query")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo){
        return bpmnInfoService.query(qo);
    }

    //新增
    @RequiresPermissions("system:bpmnInfo:addSave")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BpmnInfo bpmnInfo){
        bpmnInfoService.save(bpmnInfo);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:bpmnInfo:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(BpmnInfo bpmnInfo){
        bpmnInfoService.updateInfo(bpmnInfo);
        return AjaxResult.success();
    }

    //文件上传操作
    @RequiresPermissions("system:bpmnInfo:upload")
    @RequestMapping("/upload")
    @ResponseBody
    public AjaxResult upload(MultipartFile file){
        String upload = bpmnInfoService.upload(file);
        return AjaxResult.success("上传成功",upload);
    }

    //部署流程
    @RequiresPermissions("system:bpmnInfo:deploy")
    @RequestMapping("/deploy")
    @ResponseBody
    public AjaxResult deploy(String bpmnPath, String bpmnType, String info) throws FileNotFoundException {
        bpmnInfoService.deploy(bpmnPath, bpmnType, info);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:bpmnInfo:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        bpmnInfoService.deleteBatch(ids);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:bpmnInfo:delete")
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id){
        bpmnInfoService.delete(id);
        return AjaxResult.success();
    }

    //响应回二进制文件
    @RequestMapping("/readResource")
    public void readResource(String deploymentId, String type, HttpServletResponse response) throws IOException{
        InputStream stream = bpmnInfoService.readResource(deploymentId,type);
        IOUtils.copy(stream,response.getOutputStream());
    }

}
