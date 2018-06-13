package com.winit.repository;

import com.winit.dataobject.UserCnee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-10-26 11:28
 */
public interface UserCneeRepository extends PagingAndSortingRepository<UserCnee, String> {

	Page<UserCnee> findAll(Specification<UserCnee> uerCnee,Pageable pageable);
}
