package com.winit.service;

import com.winit.dataobject.ProductInfo;
import com.winit.dto.CartDTO;
import com.winit.enums.ProductStatusEnum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品
 * Created by liyou
 * 2017-05-09 17:27
 */
public interface ProductService {

    ProductInfo findOne(Long productId);

    /**
     * 查询所有在架商品列表
     * @return
     */
    List<ProductInfo> findUpAll();

    Page<ProductInfo> findAll(Pageable pageable);

    ProductInfo save(ProductInfo productInfo);
    
    
    /**
     * 查询该卖家的所有商品
     * @param userWxOpenid
     * @return
     */
    Page<ProductInfo> findByUserWxOpenid(String userWxOpenid,Pageable pageable);

    //加库存
    void increaseStock(List<CartDTO> cartDTOList);

    //减库存
    void decreaseStock(List<CartDTO> cartDTOList);

    //上架
    ProductInfo onSale(Long productId);

    //下架
    ProductInfo offSale(Long productId);

    Page<ProductInfo> findByUserWxOpenidAndAuditStatus(Pageable pageable,String userWxOpenid,String auditStatus);
    
    /**  Created by yhy
     * 查询卖家下所有的商品信息
     * @param userWxOpenid
     * @param auditStatus
     * @return
     */
    List<ProductInfo> findAll(final ProductInfo productInfo,Date startDate,Date endDate);
    
    /**  Created by yhy
     * 根据id集合查询商品
     * @param ids
     * @return
     */
    List<ProductInfo> findListByIds(List<Long> ids);
    
    /**
     * Created by yyl
     * 查询所有上架商品，带分页
     * @param pageable
     * @return
     */
    public Page<ProductInfo> findUpAll(Pageable pageable);
    
    /**
     * 查询该卖家的所有商品
     * @return
     */
    Page<ProductInfo> findAllByProductInfo(ProductInfo productInfo,Pageable pageable);
    

    /**
     * 查询该卖家的所有商品
     * @param userWxOpenid
     * @return
     */
    List<ProductInfo> findByUserWxOpenid(String userWxOpenid);
    
    /**
	 * 通过译者微信号获取商品信息
	 * @param userWxOpenids
	 * @return
	 */
	public List<ProductInfo> findByUserWxOpenids(List<String> userWxOpenids) ;

}
