package com.winit.controller;

import com.winit.VO.ResultVO;
import com.winit.dataobject.BannerInfo;
import com.winit.dataobject.ProductCategory;
import com.winit.exception.SellException;
import com.winit.service.BannerInfoService;
import com.winit.service.CategoryService;
import com.winit.utils.ResultVOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 卖家类目
 * Created by liyou
 * 2017-07-23 21:06
 */
@RestController
@RequestMapping("/seller/banner")
public class BannerInfoController {

    @Autowired
    private BannerInfoService bannerInfoService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 类目列表
     * @return
     */
    @GetMapping("/list")
    public ResultVO<Map<String, String>> list(@RequestParam(value = "isvalid",required = false, defaultValue = "1") String isvalid,
                                              @RequestParam(required = false, value = "isBack", defaultValue = "") String isBack) {

        List<BannerInfo> bannerInfos = bannerInfoService.findByIsValid(isvalid);

       if (StringUtils.equals("all",isvalid)){
           bannerInfos=bannerInfoService.findAll();
       }
        Collections.sort(bannerInfos, Comparator.comparing(BannerInfo::getQueue));
        Map<String, Object> map = new HashMap<>();
        map.put("bannerList",bannerInfos);

        if (!StringUtils.equals("all",isvalid)) {

            List<ProductCategory> productCategories = categoryService.findByPid("0");
            Collections.sort(productCategories, Comparator.comparing(ProductCategory::getQueue));

            List<ProductCategory> productCategories_new = new ArrayList<>();
            int i = 0;
            for (ProductCategory obj : productCategories) {
                if (i <= 6) {
                    List<ProductCategory> productCategorieschild = categoryService.findByPid(String.valueOf(obj.getId()));
                    if (productCategorieschild.size() > 0) {
                        obj.setClickUrl("/sell/seller/product/list?productType=" + productCategorieschild.get(0).getId() + "&auditStatus=1");
                    }else {
                        obj.setClickUrl("/sell/seller/product/list?productType=9&auditStatus=1");
                    }
                    productCategories_new.add(obj);
                }
                i++;
            }
            if (!StringUtils.equals("back", isBack))
                map.put("categoryList", productCategories_new);
        }
        return ResultVOUtil.success(map);
    }
    /**
     * 保存/更新
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping("/save")
    public ResultVO<Map<String, String>> save(@RequestBody BannerInfo form, BindingResult bindingResult) {

        BannerInfo bannerInfo = new BannerInfo();
        Map<String, String> map = new HashMap<>();
        try {
            if (bannerInfo.getId() != null) {
                bannerInfo = bannerInfoService.findOne(form.getId());
            }
            BeanUtils.copyProperties(form, bannerInfo);
            bannerInfoService.save(bannerInfo);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            return ResultVOUtil.error(0, "添加出现异常");
        }
        return ResultVOUtil.success();
    }
    
    
    
}
