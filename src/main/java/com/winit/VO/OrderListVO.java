package com.winit.VO;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.winit.dataobject.OrderDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* �����б�
 */

@Data
public class OrderListVO {
	 /**
     * ����id
     */
    private Long id;
    /**
     * ��������id
     */
    private Long orderDetailId;

    /**
     * �������
     */
    private String orderNo;
    
    /**
     * ��Ʒ����
     */
    private String productName;
    /**
     * ��ǰ�۸�,��λ��
     */
    private BigDecimal productPrice;
    
    /**
     * ����
     */
    private Integer productQuantity;
    /**
     * �����ܼ�
     */
    private BigDecimal orderAmount;
    

    /**
     * �������
     */
    private String buyerName;

    /**
     * ��ҵ绰
     */
    private String buyerPhone;

    /**
     * ��ҵ�ַ
     */
    private String buyerAddress;

    /**
     * ���΢��OPENID
     */
    private String buyerOpenid;


    /**
     * ����״̬��1:��֧�� 2:��ȡ�� 3:��֧�� 4:�ѷ��� 5:����� 6:������ 7:�˿��� 8:���˿� 9����ɾ����
     */
    private String orderStatus;
    
    /**
     * EBAY������
     */
    private String ebayNo;

    /**
     * EBAY״̬��1:��֧�� 2:��ȡ�� 3:��֧�� 4:�ѷ��� 5:�����-�������ǩ�գ�
     */
    private String ebayStatus;
    
    /**
     * 订单组编号
     */
    private String orderGroupNo;
    
    /**
     * 商品选择属性
     */
    private String productAttr ;
    /**
     * ����ʱ��
     */
    private Date created ;
    
    List<OrderDetail> orderDetailList = new ArrayList<>();
}