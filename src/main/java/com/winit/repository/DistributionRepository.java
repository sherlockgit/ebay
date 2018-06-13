package com.winit.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.winit.dataobject.Distribution;

/**
 * Created by yhy
 * 2017-10-28 17:28
 */
public interface DistributionRepository extends PagingAndSortingRepository<Distribution, Long> {
	
	List<Distribution> findAll(Specification<Distribution> accountItem);
	
	Page<Distribution> findAll(Specification<Distribution> distribution,Pageable pageable);
	
	List<Distribution> findByOrderNo(String orderNo);
}
