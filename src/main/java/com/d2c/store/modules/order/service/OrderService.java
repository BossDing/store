package com.d2c.store.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.query.OrderQuery;

import java.util.Map;

/**
 * @author BaiCai
 */
public interface OrderService extends IService<OrderDO> {

    OrderDO doCreate(OrderDO order);

    boolean doClose(OrderDO order);

    boolean doDelete(String orderSn);

    Map<String, Object> countDaily(OrderQuery query);

}
