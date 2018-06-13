package com.winit.service;

import com.winit.dataobject.WxRule;
import com.winit.dataobject.WxSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WxRuleService {

    public String createMessage(String fromUser, String toUser, String keyWords);

    public WxRule addWxRule(WxRule wxRule, WxSource wxSource);

    public void updateWxRule(WxRule wxRule, WxSource wxSource);

    public void deleteWxRule(Long id);

    public WxRule getWxRule(Long id);

    public Page<WxRule> findWxRulePage(Pageable pageable);

    public WxSource getWxSource(Long id);
}
