package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.WxMessage;
import com.winit.dataobject.WxRule;
import com.winit.dataobject.WxSource;
import com.winit.enums.MessageTypeEnum;
import com.winit.repository.WxMessageRepository;
import com.winit.service.WxMessageService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class WxMessageServiceImpl implements WxMessageService {

    @Autowired
    WxMessageRepository wxMessageRepository;

    @Override
    public String createMessage(String fromUser, String toUser, String keyWords) {
        WxMessage wxMessage = getWxMessageyKeyWords(keyWords);
        if(null==wxMessage){
            return null;
        }


        String messageText = null;

        if(wxMessage.getMessageType().equals(MessageTypeEnum.TEXT.getCode())){
            String text = wxMessage.getContent();
            WxMpXmlOutTextMessage textContent = WxMpXmlOutTextMessage.TEXT().toUser(fromUser).fromUser(toUser).content(text).build();
            messageText = textContent.toXml();
        }else if(wxMessage.getMessageType().equals(MessageTypeEnum.NEWS.getCode())){
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setDescription(wxMessage.getContent());
            item.setPicUrl(wxMessage.getPictureUrl());
            item.setTitle(wxMessage.getMessageName());
            item.setUrl(wxMessage.getNewsUrl());
            WxMpXmlOutNewsMessage news = WxMpXmlOutTextMessage.NEWS().toUser(fromUser).fromUser(toUser).addArticle(item).build();
            messageText = news.toXml();
        }

        return messageText;
    }

    @Override
    public WxMessage addWxMessage(WxMessage wxMessage) {
        wxMessage.setStatus("1");
        wxMessageRepository.save(wxMessage);
        return wxMessage;
    }

    @Override
    public void updateWxMessage(WxMessage wxMessage) {
        wxMessageRepository.save(wxMessage);
    }

    @Override
    public void deleteWxMessage(Long id) {
        WxMessage wxMessage = wxMessageRepository.findOne(id);
        wxMessage.setStatus("2");
        wxMessageRepository.save(wxMessage);
    }

    @Override
    public WxMessage getWxMessage(Long id) {
        WxMessage wxMessage = wxMessageRepository.findOne(id);
        return wxMessage;
    }

    @Override
    public Page<WxMessage> findWxMessagePage(Pageable pageable) {
        Specification<WxMessage> spec = new Specification<WxMessage>() {
            public Predicate toPredicate(Root<WxMessage> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();

                predicate.add(cb.equal(root.get("status").as(String.class),"1"));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };
        return wxMessageRepository.findAll(spec,pageable);
    }

    private WxMessage getWxMessageyKeyWords(String keyWords){

        Specification<WxMessage> spec = new Specification<WxMessage>() {
            public Predicate toPredicate(Root<WxMessage> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();

                predicate.add(cb.like(root.get("keyWords").as(String.class),"%" + keyWords + "%"));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };

        List<WxMessage> list = wxMessageRepository.findAll(spec);
        if(null!=list && list.size()>0){
            return list.get(0);
        }else{
            return null;
        }

    }
}
