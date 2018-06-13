package com.winit.repository;

import com.winit.dataobject.WxMenu;
import com.winit.dataobject.WxRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface WxRuleRepository extends PagingAndSortingRepository<WxRule, Long> {

	List<WxRule> findAll(Specification<WxRule> wxRule);
	
	Page<WxRule> findAll(Specification<WxRule> wxRule, Pageable pageable);
}
