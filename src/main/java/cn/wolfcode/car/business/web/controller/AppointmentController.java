package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("/business/appointment")
public class AppointmentController {
    //模板前缀
    private static final String prefix = "business/system/appointment/";

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private IStatementService statementService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:appointment:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:appointment:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:appointment:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("appointment", appointmentService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:appointment:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Appointment> query(AppointmentQuery qo){
        return appointmentService.query(qo);
    }

    //新增
    @RequiresPermissions("system:appointment:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Appointment appointment){
        appointmentService.save(appointment);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:appointment:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Appointment appointment){
        appointmentService.saveEdit(appointment);
        return AjaxResult.success();
    }

    //客户取消订单
    @RequiresPermissions("system:appointment:cancelHandler")
    @RequestMapping("/cancelHandler")
    @ResponseBody
    public AjaxResult cancelHandler(Long id){
        appointmentService.cancelHandler(id);
        return AjaxResult.success();
    }

    //客户到店
    @RequiresPermissions("system:appointment:arrivalHandler")
    @RequestMapping("/arrivalHandler")
    @ResponseBody
    public AjaxResult arrivalHandler(Long id){
        appointmentService.arrivalHandler(id,new Date());
        return AjaxResult.success();
    }

    //删除（软删除）
    @RequiresPermissions("system:appointment:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(Long ids){
        appointmentService.softDelete(ids);
        return AjaxResult.success();
    }


    //预约单转成结算单
    @RequiresPermissions("system:appointment:generateStatement")
    @RequestMapping("/generateStatement")
    public String generateStatement(Long appointmentId){
        //生成结算单
        Statement statement =statementService.generateStatement(appointmentId);
        return "redirect:/business/statementItem/itemDetail?statementId=" + statement.getId();
    }
}
