package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("/business/statementItem")
public class StatementItemController {
    //模板前缀
    private static final String prefix = "business/system/statementItem/";

    @Autowired
    private IStatementItemService statementItemService;

    @Autowired
    private IStatementService statementService;

    @Autowired
    private IUserService userService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:statementItem:listPage")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:statementItem:addPage")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:statementItem:editPage")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("statementItem", statementItemService.get(id));
        return prefix + "edit";
    }

    //TODO: woko,这啥呀
    @RequiresPermissions("system:statementItem:itemDetail")
    @RequestMapping("/itemDetail")
    public String itemDetail(Long statementId,Model model){
        Statement statement = statementService.get(statementId);
        statement.setPayee(userService.get(statement.getPayeeId()));
        //结算单
        model.addAttribute("statement", statement);
        if(Statement.STATUS_CONSUME.equals(statement.getStatus())){
            //消费中跳到editDetail.html
            return prefix + "editDetail";
        }else{
            //已支付跳到showDetail.html
            return prefix + "showDetail";
        }
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:statementItem:query")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<StatementItem> query(StatementItemQuery qo){
        return statementItemService.query(qo);
    }

    //新增
    @RequiresPermissions("system:statementItem:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(StatementItem statementItem){
        statementItemService.save(statementItem);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:statementItem:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(StatementItem statementItem){
        statementItemService.save(statementItem);
        return AjaxResult.success();
    }

    //保存单项
    //@RequestBody 前端发送请求如果是json格式，需要使用@RequestBody这个标签告诉springmvc使用json方式进行解析
    @RequiresPermissions("system:statementItem:saveItems")
    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestBody List<StatementItem> statementItems){
        statementItemService.saveItems(statementItems);
        return AjaxResult.success();
    }


    //确认支付
    @RequiresPermissions("system:statementItem:payStatement")
    @RequestMapping("/payStatement")
    @ResponseBody
    public AjaxResult payStatement(Long statementId){
        statementItemService.payStatement(statementId);
        return AjaxResult.success();
    }
}
