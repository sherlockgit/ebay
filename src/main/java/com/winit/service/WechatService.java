package com.winit.service;

import com.winit.dataobject.WxMenu;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WechatService {

    public Map addMenu(WxMenu wxMenu);

    public void updateMenu(WxMenu wxMenu);

    public void deleteMenu(Long id);

    public WxMenu getMenu(Long id);

    public Page<WxMenu> getMenuList(Pageable pageable);

    public boolean synchroWxMenu();

    public List<WxMenu> getTopMenu();

    public List<WxMenu> getMenuByParentId(Long id);

}
