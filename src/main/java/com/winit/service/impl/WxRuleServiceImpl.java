package com.winit.service.impl;

import com.google.common.collect.Lists;
import com.winit.dataobject.WxRule;
import com.winit.dataobject.WxSource;
import com.winit.enums.MessageTypeEnum;
import com.winit.repository.WxRuleRepository;
import com.winit.repository.WxSourceRepository;
import com.winit.service.WxRuleService;
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
public class WxRuleServiceImpl implements WxRuleService {

    @Autowired
    WxRuleRepository wxRuleRepository;

    @Autowired
    WxSourceRepository wxSourceRepository;

    @Override
    public String createMessage(String fromUser, String toUser, String keyWords) {

        WxRule wxRule = getWxRuleByKeyWords(keyWords);
        if(null==wxRule){
            return null;
        }

        String sourceId = wxRule.getSourceId();
        WxSource wxSource = wxSourceRepository.findOne(Long.valueOf(sourceId));
        String messageText = null;

        if(wxRule.getRuleType().equals(MessageTypeEnum.TEXT.getCode())){
            String text = wxSource.getContent();
            WxMpXmlOutTextMessage textContent = WxMpXmlOutTextMessage.TEXT().toUser(fromUser).fromUser(toUser).content(text).build();
            messageText = textContent.toXml();
        }else if(wxRule.getRuleType().equals(MessageTypeEnum.NEWS.getCode())){
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setDescription(wxSource.getContent());
            item.setPicUrl(wxSource.getContent());
            item.setTitle(wxRule.getRuleName());
            item.setUrl(wxSource.getSourceUrl());
            WxMpXmlOutNewsMessage news = WxMpXmlOutTextMessage.NEWS().toUser(fromUser).fromUser(toUser).addArticle(item).build();
            messageText = news.toXml();
        }

        return messageText;
    }

    private WxRule getWxRuleByKeyWords(String keyWords){

        Specification<WxRule> spec = new Specification<WxRule>() {
            public Predicate toPredicate(Root<WxRule> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = Lists.newArrayList();

                predicate.add(cb.like(root.get("keyWords").as(String.class),"%" + keyWords + "%"));

                Predicate[] pre = new Predicate[predicate.size()];
                return query.where(predicate.toArray(pre)).getRestriction();
            }
        };

        List<WxRule> list = wxRuleRepository.findAll(spec);
        if(null!=list && list.size()>0){
            return list.get(0);
        }else{
            return null;
        }

    }

    @Override
    public WxRule addWxRule(WxRule wxRule, WxSource wxSource) {
        wxRuleRepository.save(wxRule);
        wxSourceRepository.save(wxSource);
        return wxRule;
    }

    @Override
    public void updateWxRule(WxRule wxRule, WxSource wxSource) {
        wxRuleRepository.save(wxRule);
        wxSourceRepository.save(wxSource);
    }

    @Override
    public void deleteWxRule(Long id) {
        wxRuleRepository.delete(id);
    }

    @Override
    public WxRule getWxRule(Long id) {
        return wxRuleRepository.findOne(id);
    }

    @Override
    public Page<WxRule> findWxRulePage(Pageable pageable) {
        Page<WxRule> page = wxRuleRepository.findAll(pageable);
        return page;
    }

    @Override
    public WxSource getWxSource(Long id) {
        WxSource wxSource = wxSourceRepository.findOne(id);
        return wxSource;
    }
}
