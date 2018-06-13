package com.winit.repository;

import com.winit.dataobject.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-06-11 17:28
 */
public interface AccountRepository extends PagingAndSortingRepository<Account, String> {

	Account findByUserId(String userId);
	
	Page<Account> findAll(Specification<Account> account,Pageable pageable);
	
}
