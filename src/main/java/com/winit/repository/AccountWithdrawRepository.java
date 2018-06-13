package com.winit.repository;

import com.winit.dataobject.AccountItem;
import com.winit.dataobject.AccountWithdraw;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface AccountWithdrawRepository extends PagingAndSortingRepository<AccountWithdraw, String> {
	
	Page<AccountWithdraw> findAll(Specification<AccountWithdraw> accountWithdraw,Pageable pageable);
}
