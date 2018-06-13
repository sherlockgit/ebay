package com.winit.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.winit.dataobject.UserCnee;

/**

 * Created by yhy
 * 2017-10-22 13:23
 */
public interface UserCneeService {

    /** 创建 */
	UserCnee create(UserCnee UserCnee);
	
	/** 查询单条信息 */
	UserCnee selectOne(String id);
	
	/**会员地址列表**/
	Page<UserCnee> findPage(UserCnee userCnee,Pageable pageable);
	
	/**批量操作**/
	void batch(List<UserCnee> userCnees);

	List<UserCnee> selectList(List<String> ids);

}