package com.winit.repository;

import com.winit.dataobject.AccountItem;
import com.winit.dataobject.WxMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface WxMenuRepository extends PagingAndSortingRepository<WxMenu, Long> {

	List<WxMenu> findAll(Specification<WxMenu> wxMenu);

	Page<WxMenu> findAll(Specification<WxMenu> wxMenu, Pageable pageable);
}
