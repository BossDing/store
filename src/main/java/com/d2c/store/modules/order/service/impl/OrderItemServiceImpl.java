package com.d2c.store.modules.order.service.impl;

import com.d2c.store.common.api.base.BaseService;
import com.d2c.store.modules.order.mapper.OrderItemMapper;
import com.d2c.store.modules.order.model.OrderItemDO;
import com.d2c.store.modules.order.query.OrderItemQuery;
import com.d2c.store.modules.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author BaiCai
 */
@Service
public class OrderItemServiceImpl extends BaseService<OrderItemMapper, OrderItemDO> implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Map<String, Object> countDaily(OrderItemQuery query) {
        return orderItemMapper.countDaily(query);
    }

}
