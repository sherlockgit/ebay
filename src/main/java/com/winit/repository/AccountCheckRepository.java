package com.winit.repository;

import com.winit.dataobject.AccountCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface AccountCheckRepository extends PagingAndSortingRepository<AccountCheck, String> {
	
	
	Page<AccountCheck> findAll(Specification<AccountCheck> accountCheck,Pageable pageable);
}
