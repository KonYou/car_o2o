package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/carPackageAudit")
public class CarPackgeAuditController {
    //模板前缀
    private static final String prefix = "business/system/carPackageAudit/";

    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:carPackageAudit:listPage")
    @RequestMapping("/listPage")
    public String listPage() {
        return prefix + "list";
    }

    //@RequiresPermissions("system:carPackageAudit:todoPage")
    @RequestMapping("/todoPage")
    public String todoPage() {
        return prefix + "todoPage";
    }

    //@RequiresPermissions("system:carPackageAudit:auditPage")
    @RequestMapping("/auditPage")
    public String auditPage(Long id, Model model) {
        model.addAttribute("id", id);
        return prefix + "audit";
    }

    //@RequiresPermissions("system:carPackageAudit:donePage")
    @RequestMapping("/donePage")
    public String donePage() {
        return prefix + "donePage";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:carPackageAudit:query")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        return carPackageAuditService.query(qo);
    }

    //    @RequiresPermissions("system:carPackageAudit:todoQuery")
    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> todoQuery(CarPackageAuditQuery qo) {
        User user = ShiroUtils.getUser();
        qo.setAuditorId(user.getId());
        qo.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        return carPackageAuditService.query(qo);
    }

    //    @RequiresPermissions("system:carPackageAudit:doneQuery")
    @RequestMapping("/doneQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> doneQuery(CarPackageAuditQuery qo) {
        String userName = ShiroUtils.getUser().getUserName();
        qo.setAuditRecord(userName);
        return carPackageAuditService.query(qo);
    }

    //新增
    @RequiresPermissions("system:carPackageAudit:addSave")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(CarPackageAudit carPackageAudit) {
        carPackageAuditService.save(carPackageAudit);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:carPackageAudit:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(CarPackageAudit carPackageAudit) {
        carPackageAuditService.update(carPackageAudit);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:carPackageAudit:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        carPackageAuditService.deleteBatch(ids);
        return AjaxResult.success();
    }

    //完成审批
    //@RequiresPermissions("system:carPackageAudit:audit")
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id, Integer auditStatus,String info) {
        carPackageAuditService.audit(id,auditStatus,info);
        return AjaxResult.success();
    }

    //查看审核进度
    //@RequiresPermissions("system:carPackageAudit:processImg")
    @RequestMapping("/processImg")
    public void processImg(Long id, HttpServletResponse response) throws IOException {
        InputStream stream = carPackageAuditService.processImg(id);
        IOUtils.copy(stream, response.getOutputStream());
    }

    //撤销
    @RequiresPermissions("system:carPackageAudit:cancelApply")
    @RequestMapping("/cancelApply")
    @ResponseBody
    public AjaxResult cancelApply(Long id) {
        carPackageAuditService.cancelApply(id);
        return AjaxResult.success();
    }
}
