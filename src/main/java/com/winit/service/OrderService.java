package com.winit.service;

import java.util.Date;
import java.util.List;

import com.winit.VO.OrderListVO;
import com.winit.form.OrderListFrom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.winit.dataobject.OrderDetail;
import com.winit.dataobject.OrderMaster;
import com.winit.dto.OrderDTO;

/**
 * Created by liyou
 * 2017-06-11 18:23
 */
public interface OrderService {

    /** 创建订单. */
    OrderDTO create(OrderDTO orderDTO);
    
    /** 编辑订单. */
    OrderDTO save(OrderDTO orderDTO);
    
    /** 查询单个订单. */
    OrderDTO findOne(String orderId);

    /** 查询订单列表. */
    Page<OrderDTO> findList(String buyerOpenid,String orderStatus,Integer isContainDistributor, Pageable pageable);

    /** 取消订单. */
    OrderDTO cancel(OrderDTO orderDTO);

    /** 完结订单. */
    OrderDTO finish(OrderDTO orderDTO);

    /** 支付订单. */
    OrderMaster paid(OrderMaster orderMaster);

    /** 更新订单ORDER_GROUP_NO 方便二次支付**/
    OrderMaster updateGroupNo(OrderMaster orderMaster);

    /** 查询订单列表. */
    Page<OrderDTO> findList(Pageable pageable);
    
    /**根据商品ID集合查询订单集合    yhy*/
    List<OrderDetail> findAll(final OrderDetail orderDetail,final List<Long> productIds,List<Long> orderIds,Date startDate,Date endDate);
    
    /**根据订单ID集合查询订单详细信息列表分页    yhy*/
    Page<OrderMaster> findOrderMasterPage(final OrderMaster orderMaster,List<String> orderIds,Pageable pageable);
    
    /**根据订单ID集合查询订单详细信息列表    yhy*/
    List<OrderMaster> findOrderMasterList(final OrderMaster orderMaster,List<String> orderIds);

    /**
     * 根据订单组编号查询订单列表
     * @param orderGroupNo
     * @return
     */
    List<OrderMaster> findOrderMasterList(String orderGroupNo);

    /**
     * 通过订单号查询订单列表
     * @param orderNo
     * @return
     */
    List<OrderMaster> findOrderMasterByOrderNo(String orderNo);
    
    /**查询订单详列表    yyl*/
    List<OrderMaster> findOrderMasterByNameList(final OrderMaster orderMaster,List<String> buyerNames);
    
    /** 根据orderId查询订单详情. */
    List<OrderDetail> findByOrderId(String orderId);
    /** 取消订单. */
    OrderDTO cancel(String openid, String orderId);
    
    Page<OrderDetail> findOrderDetailByOrderNoList(List<String> orderNoLsit,Pageable pageable) ;
    
    Page<OrderMaster> findOrderMasterPage(OrderMaster orderMaster,Pageable pageable);
    
    /** 查询订单信息. */
    List<OrderMaster> findOrderMasterList(OrderMaster orderMaster,Date startDate,Date endDate);
    
    /**根据订单ID集合查询订单详细信息列表    yyl*/
    List<OrderDetail> findOrderDetailList(List<String> orderIds);
    //更新订单
    void update(OrderListFrom orderListFrom);
    /** 通过openId获取分销商订单. */
    List<String> findOderIdsByOpenId(String openId,Integer isContainDistributor);
    /**
	 * 通过商品id获取订单信息
	 * @param productIds
	 * @return
	 */
	public List<OrderDetail> findOrderDetailByProductIds(List<Long> productIds);
	/**
	 * 查询订单列表
	 * @param orderMaster
	 * @return
	 */
	public List<OrderMaster> findOrderMasterList(OrderMaster orderMaster);
	/**
	 * 根据订单编号分页查询订单信息
	 * @param orderNos
	 * @param pageable
	 * @return
	 */
	public Page<OrderMaster> findOrderMasterByOrderNos(List<String> orderNos,String orderStatus, Pageable pageable);
	/** 通过openId获分销商订单. */
    public List<String> findDistributedOderIdsByOpenIds(List<String> openIds);
}
