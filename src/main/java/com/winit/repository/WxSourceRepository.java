package com.winit.repository;

import com.winit.dataobject.WxSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface WxSourceRepository extends PagingAndSortingRepository<WxSource, Long> {

	List<WxSource> findAll(Specification<WxSource> wxSource);
	
	Page<WxSource> findAll(Specification<WxSource> wxSource, Pageable pageable);
}
