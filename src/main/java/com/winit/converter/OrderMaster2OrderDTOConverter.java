package com.winit.converter;

import com.winit.dataobject.OrderMaster;
import com.winit.dto.OrderDTO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyou
 * 2017-06-11 22:02
 */
public class OrderMaster2OrderDTOConverter {

    public static OrderDTO convert(OrderMaster orderMaster) {

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster, orderDTO);
        return orderDTO;
    }

    public static List<OrderDTO> convert(List<OrderMaster> orderMasterList) {
        List<OrderDTO> list = new ArrayList<OrderDTO>();
        for(OrderMaster e : orderMasterList){
            OrderDTO orderDTO = convert(e);
            list.add(orderDTO);
        }
        return list;
    }
}
