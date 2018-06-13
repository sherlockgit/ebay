package com.winit.controller;

import com.winit.VO.ResultVO;
import com.winit.dataobject.ProductCategory;
import com.winit.exception.SellException;
import com.winit.form.CategoryForm;
import com.winit.service.CategoryService;
import com.winit.utils.ResultVOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

/**
 * 卖家类目
 * Created by liyou
 * 2017-07-23 21:06
 */
@RestController
@RequestMapping("/seller/category")
public class SellerCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 类目列表
     * @return
     */
    @GetMapping("/list")
    public ResultVO<Map<String, String>> list(@RequestParam(value = "pid", required = false) String pid) {

        if(StringUtils.isEmpty(pid)){
            pid="0";
        }
        List<ProductCategory> categoryList = categoryService.findByPid(pid);

        Collections.sort(categoryList, Comparator.comparing(ProductCategory::getQueue));

        return ResultVOUtil.success(categoryList);
    }

    /**
     * 保存/更新
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping("/save")
    public ResultVO<Map<String, String>> save(@RequestBody ProductCategory form, BindingResult bindingResult) {

        ProductCategory productCategory = new ProductCategory();
        Map<String, String> map = new HashMap<>();
        try {
            if (productCategory.getId() != null) {
                productCategory = categoryService.findOne(form.getId());
            }
            BeanUtils.copyProperties(form, productCategory);
            categoryService.save(productCategory);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            return ResultVOUtil.error(0, "添加品类出现异常");
        }
        return ResultVOUtil.success();
    }

    /**
     * 类目列表
     * @return
     */
    @GetMapping("/queryById")
    public ResultVO<Map<String, String>> queryById(@RequestParam(value = "productType", required = false) String productType) {

        if(StringUtils.isEmpty(productType)){
            productType="0";
        }
        ProductCategory productCategory = categoryService.findOne(Integer.parseInt(productType));

        return ResultVOUtil.success(productCategory);
    }
    
}
