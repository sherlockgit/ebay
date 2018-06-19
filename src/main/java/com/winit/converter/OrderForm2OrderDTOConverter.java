package com.winit.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.winit.dataobject.OrderDetail;
import com.winit.dto.OrderDTO;
import com.winit.form.OrderForm;
import com.winit.utils.BeanUtilEx;

/**
 * Created by liyou
 * 2017-06-18 23:41
 */
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        OrderDTO orderDTO = new OrderDTO();
        if(null != orderForm.getOrderMasterId()){
        	orderDTO.setId(orderForm.getOrderMasterId());
        }
         
        BeanUtilEx.copyPropertiesIgnoreNull(orderForm, orderDTO);
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());
        orderDTO.setCarriageFee(orderForm.getCarriage());
        orderDTO.setTaxFee(orderForm.getTaxFee());
        orderDTO.setCneeIdcard(orderForm.getCneeIdcard());
        if(orderDTO.getCarriageFee() ==null){
        	orderDTO.setCarriageFee(new BigDecimal(BigInteger.ZERO));
        }
        if(orderDTO.getTaxFee() ==null){
            orderDTO.setTaxFee(new BigDecimal(BigInteger.ZERO));
        }
        List<OrderDetail> orderDetailList = orderForm.getItems();
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }
}
