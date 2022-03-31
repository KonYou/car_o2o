package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.BusServiceItem;
import cn.wolfcode.car.business.query.BusServiceItemQuery;
import cn.wolfcode.car.business.service.IBusServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.util.DateUtils;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 *
 */
@Controller
@RequestMapping("business/serviceItem")
public class BusServiceItemController {
    //模板前缀
    private static final String prefix = "business/system/serviceItem/";

    @Autowired
    private IBusServiceItemService busServiceItemService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:serviceItem:listPage")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:serviceItem:addPage")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:serviceItem:editPage")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("serviceItem", busServiceItemService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:serviceItem:query")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BusServiceItem> query(BusServiceItemQuery qo){
        return busServiceItemService.query(qo);
    }

    @RequiresPermissions("system:serviceItem:selectAllSaleOnList")
    @RequestMapping("/selectAllSaleOnList")
    @ResponseBody
    public TablePageInfo<BusServiceItem> selectAllSaleOnList(BusServiceItemQuery qo){
        qo.setSaleStatus(BusServiceItem.SALESTATUS_ON); //上架
        return busServiceItemService.query(qo);
    }

/*
    //判断name是否唯一
    @RequestMapping("/checkServiceItemNameUnique")
    @ResponseBody
    public String  checkServiceItemNameUnique(String name){
        boolean ret = busServiceItemService.checkNameExsit(name);
        return ret?"1":"0";
    }

    //判断code是否唯一
    @RequestMapping("/checkServiceItemCodeUnique")
    @ResponseBody
    public String  checkServiceItemCodeUnique(String code){
        boolean ret = busServiceItemService.checkCodeExsit(code);
        return ret?"1":"0";
    }
*/

    //新增
    @RequiresPermissions("system:serviceItem:addSave")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BusServiceItem serviceItem){
        busServiceItemService.save(serviceItem);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:serviceItem:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(BusServiceItem serviceItem){
        busServiceItemService.saveEdit(serviceItem);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:serviceItem:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        busServiceItemService.deleteBatch(ids);
        return AjaxResult.success();
    }

    @RequiresPermissions("system:serviceItem:shelfOn")
    @RequestMapping("/shelfOn")
    @ResponseBody
    public AjaxResult shelfOn(String id){
        busServiceItemService.shelfOn(id);
        return AjaxResult.success();
    }

    @RequiresPermissions("system:serviceItem:takeDown")
    @RequestMapping("/takeDown")
    @ResponseBody
    public AjaxResult takeDown(String id){
        busServiceItemService.takeDown(id);
        return AjaxResult.success();
    }
}
