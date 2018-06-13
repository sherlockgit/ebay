package com.winit.service;

import com.winit.dataobject.WxMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WxMessageService {

    public String createMessage(String fromUser, String toUser, String keyWords);

    public WxMessage addWxMessage(WxMessage wxMessage);

    public void updateWxMessage(WxMessage wxMessage);

    public void deleteWxMessage(Long id);

    public WxMessage getWxMessage(Long id);

    public Page<WxMessage> findWxMessagePage(Pageable pageable);

}
