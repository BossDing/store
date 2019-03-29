package com.d2c.store.modules.order.service.impl;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.store.common.api.base.BaseService;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.order.mapper.OrderMapper;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.model.OrderItemDO;
import com.d2c.store.modules.order.query.OrderItemQuery;
import com.d2c.store.modules.order.query.OrderQuery;
import com.d2c.store.modules.order.service.OrderItemService;
import com.d2c.store.modules.order.service.OrderService;
import com.d2c.store.modules.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiCai
 */
@Service
public class OrderServiceImpl extends BaseService<OrderMapper, OrderDO> implements OrderService {

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductSkuService productSkuService;

    @Override
    @Transactional
    public boolean save(OrderDO entity) {
        boolean success = super.save(entity);
        return success;
    }

    @Override
    @Transactional
    public OrderDO doCreate(OrderDO order) {
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        if (orderItemList.size() == 0) {
            throw new ApiException("订单明细不能为空");
        }
        // 创建订单
        this.save(order);
        for (OrderItemDO orderItem : orderItemList) {
            // 扣减库存
            int success = productSkuService.doDeductStock(orderItem.getProductSkuId(), orderItem.getProductId(), orderItem.getQuantity());
            if (success == 0) {
                throw new ApiException(orderItem.getProductSkuId() + "的SKU库存不足");
            }
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getSn());
            orderItem.setType(order.getType());
            orderItemService.save(orderItem);
        }
        return order;
    }

    @Override
    @Transactional
    public boolean doClose(OrderDO order) {
        boolean success = true;
        // 关闭订单
        OrderDO o = new OrderDO();
        o.setId(order.getId());
        o.setStatus(OrderDO.StatusEnum.CLOSED.name());
        success &= this.updateById(o);
        // 关闭订单明细
        OrderItemDO oi = new OrderItemDO();
        oi.setStatus(OrderItemDO.StatusEnum.CLOSED.name());
        OrderItemQuery oiq = new OrderItemQuery();
        oiq.setOrderSn(new String[]{order.getSn()});
        success &= orderItemService.update(oi, QueryUtil.buildWrapper(oiq));
        List<OrderItemDO> orderItemList = order.getOrderItemList();
        for (OrderItemDO orderItem : orderItemList) {
            // 返还库存
            productSkuService.doReturnStock(orderItem.getProductSkuId(), orderItem.getProductId(), orderItem.getQuantity());
        }
        return success;
    }

    @Override
    @Transactional
    public boolean doDelete(String orderSn) {
        boolean success = true;
        OrderQuery oq = new OrderQuery();
        oq.setSn(orderSn);
        success &= this.remove(QueryUtil.buildWrapper(oq));
        OrderItemQuery oiq = new OrderItemQuery();
        oiq.setOrderSn(new String[]{orderSn});
        success &= orderItemService.remove(QueryUtil.buildWrapper(oiq));
        return success;
    }

}
