package com.winit.repository;

import com.winit.dataobject.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by yhy
 * 2017-10-21 17:28
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {

	Page<User> findAll(Specification<User> user,Pageable pageable);
	
	List<User> findAll(Specification<User> user);
	
	User findByUserWxOpenid(String userWxOpenid);
}
