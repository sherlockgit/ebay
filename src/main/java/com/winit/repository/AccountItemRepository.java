package com.winit.repository;

import com.winit.dataobject.AccountItem;
import com.winit.dataobject.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface AccountItemRepository extends PagingAndSortingRepository<AccountItem, String> {
	
	List<AccountItem> findAll(Specification<AccountItem> accountItem);
	
	Page<AccountItem> findAll(Specification<AccountItem> accountItem,Pageable pageable);
}
