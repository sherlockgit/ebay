package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.WxMenu;
import com.winit.repository.WxMenuRepository;
import com.winit.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WechatServiceImpl implements WechatService {

    private static class WechatMenu extends me.chanjar.weixin.common.bean.menu.WxMenu{};

    @Autowired
    WxMpService wxMpService;

    @Autowired
    WxMenuRepository wxMenuRepository;

    @Override
    public Map addMenu(WxMenu wxMenu){

        Map<String, String> reuslt = new HashMap<String, String>();

        if(null==wxMenu.getWxMenuParent()){
             List list = getTopMenu();
            wxMenu.setWxMenuLevel(1);
             if(null!=list && list.size()>=3){
                 reuslt.put("result","false");
                 reuslt.put("msg","一级菜单只能添加3个！");
                 return reuslt;
             }

        }else{
            List list = getMenuByParentId(wxMenu.getWxMenuParent().longValue());
            wxMenu.setWxMenuLevel(2);
            if(null!=list && list.size()>=5){
                reuslt.put("result","false");
                reuslt.put("msg","二级菜单只能添加5个！");
                return reuslt;
            }

        }
        wxMenu = wxMenuRepository.save(wxMenu);
        reuslt.put("result","true");
        return reuslt;
    }

    @Override
    @Transactional
    public void updateMenu(WxMenu wxMenu) {

        wxMenuRepository.save(wxMenu);
    }

    @Override
    public void deleteMenu(Long id) {
        wxMenuRepository.delete(id);
    }

    @Override
    public WxMenu getMenu(Long id) {
        return wxMenuRepository.findOne(id);
    }

    @Override
    public Page<WxMenu> getMenuList(Pageable pageable) {
        Specification<WxMenu> spec = new Specification<WxMenu>() {
            public Predicate toPredicate(Root<WxMenu> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();
                //默认查询有效，未删除的
                predicate.add(cb.equal(root.get("wxMenuFlag").as(String.class), "1"));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return wxMenuRepository.findAll(spec, pageable);
    }

    @Override
    public boolean synchroWxMenu() {

        List<WxMenu> list = getTopMenu();

        List<WxMenuButton> topButton = new ArrayList<WxMenuButton>();
        WxMenu wxMenu;
        WxMenuButton wxMenuButton;
        for(int i=0 ; i<list.size() ; i++){

            wxMenu = list.get(i);
            List<WxMenu> subWxMenuList = getMenuByParentId(wxMenu.getId());
            if(subWxMenuList.size()>0){
                wxMenu.setWxMenuType("3");
            }
            wxMenuButton = transfer(wxMenu);


            if(null!=subWxMenuList && subWxMenuList.size()>0){

                WxMenu subWxMenu;
                WxMenuButton subWxMenuButton;
                List<WxMenuButton> subWxMenuButtonList = new ArrayList<WxMenuButton>();
                for(int j=0 ; j<subWxMenuList.size() ; j++){
                    subWxMenu = subWxMenuList.get(j);
                    subWxMenuButton = transfer(subWxMenu);
                    subWxMenuButtonList.add(subWxMenuButton);
                }
                wxMenuButton.setSubButtons(subWxMenuButtonList);
            }

            topButton.add(wxMenuButton);

        }
        WechatMenu wechatMenu = new WechatMenu();
        wechatMenu.setButtons(topButton);

        try {
            String str = wechatMenu.toJson();
            str = str.replace("buttons","button");
            wxMpService.getMenuService().menuCreate(str);
        } catch (WxErrorException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public List<WxMenu> getTopMenu() {

        Specification<WxMenu> spec = new Specification<WxMenu>() {
            public Predicate toPredicate(Root<WxMenu> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();
                //默认查询有效，未删除的
                predicate.add(cb.equal(root.get("wxMenuFlag").as(String.class), "1"));
                predicate.add(cb.equal(root.get("wxMenuLevel").as(Integer.class), "1"));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return wxMenuRepository.findAll(spec);
    }

    private WxMenuButton transfer(WxMenu wxMenu){

        WxMenuButton wxMenuButton = new WxMenuButton();
        wxMenuButton = new WxMenuButton();
        wxMenuButton.setName(wxMenu.getWxMenuName());

        if(wxMenu.getWxMenuType().equals("1")){
            wxMenuButton.setType("view");
            wxMenuButton.setUrl(wxMenu.getWxMenuContent());
        }else if(wxMenu.getWxMenuType().equals("2")){
            wxMenuButton.setType("click");
            wxMenuButton.setKey(wxMenu.getWxMenuContent());
        }
        return wxMenuButton;

    }

    /**
     * 获取子菜单
     * @param id
     * @return
     */
    public List<WxMenu> getMenuByParentId(Long id){
        Specification<WxMenu> spec = new Specification<WxMenu>() {
            public Predicate toPredicate(Root<WxMenu> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();
                //默认查询有效，未删除的
                predicate.add(cb.equal(root.get("wxMenuFlag").as(String.class), "1"));
                predicate.add(cb.equal(root.get("wxMenuParent").as(Integer.class), id.intValue()));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return wxMenuRepository.findAll(spec);
    }
}
