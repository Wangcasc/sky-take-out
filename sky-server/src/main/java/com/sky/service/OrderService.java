package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * @param orderSubmitDTO  the order submit DTO
     * @return the order submit VO
     */
    OrderSubmitVO submit(OrdersSubmitDTO orderSubmitDTO);

}
