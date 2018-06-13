package com.winit.repository;

import com.winit.dataobject.AccountCheck;
import com.winit.dataobject.WxMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface WxMessageRepository extends PagingAndSortingRepository<WxMessage, Long> {

	List<WxMessage> findAll(Specification<WxMessage> wxMessage);

	Page<WxMessage> findAll(Specification<WxMessage> wxMessage, Pageable pageable);
}
