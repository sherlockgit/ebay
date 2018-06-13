package com.winit.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.User;

/**

 * Created by yhy
 * 2017-10-22 13:23
 */
public interface UserService {

    /** 创建 */
	User create(User user);
	
	/** 查询单条信息 */
	User selectOne(String id);
	
	/** 根据微信ID查询用户 */
	User selectByUserWxOpenid(String userWxOpenid);
	
	/**会员列表**/
	Page<User> findPage(User user,Pageable pageable);
	
	/**批量删除**/
	void batch(List<User> users);

	List<User> selectList(List<String> ids);
	
	List<User> selectList(final User user,List<String> wxOpenIds);

}