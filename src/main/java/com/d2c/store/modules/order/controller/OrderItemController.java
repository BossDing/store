package com.d2c.store.modules.order.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.PageModel;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.api.base.BaseCtrl;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.model.OrderItemDO;
import com.d2c.store.modules.order.query.OrderItemQuery;
import com.d2c.store.modules.order.service.OrderItemService;
import com.d2c.store.modules.order.service.OrderService;
import com.d2c.store.modules.security.model.UserDO;
import com.d2c.store.modules.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "订单明细管理")
@RestController
@RequestMapping("/back/order_item")
public class OrderItemController extends BaseCtrl<OrderItemDO, OrderItemQuery> {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @ApiOperation(value = "P2P查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<Page<OrderItemDO>> list(PageModel page, OrderItemQuery query) {
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        query.setSupplierId(user.getSupplierId());
        Page<OrderItemDO> pager = (Page<OrderItemDO>) service.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "订单明细发货")
    @RequestMapping(value = "/deliver", method = RequestMethod.POST)
    public R deliverItem(Long orderItemId, String logisticsCom, String logisticsNum) {
        OrderItemDO orderItem = orderItemService.getById(orderItemId);
        Asserts.notNull(ResultCode.RESPONSE_DATA_NULL, orderItem);
        Asserts.eq(orderItem.getStatus(), OrderItemDO.StatusEnum.WAIT_DELIVER.name(), "订单明细状态异常");
        OrderItemDO entity = new OrderItemDO();
        entity.setId(orderItemId);
        entity.setLogisticsCom(logisticsCom);
        entity.setLogisticsNum(logisticsNum);
        entity.setStatus(OrderItemDO.StatusEnum.DELIVERED.name());
        orderItemService.updateById(entity);
        OrderDO entity2 = new OrderDO();
        entity2.setId(orderItem.getOrderId());
        entity2.setStatus(OrderDO.StatusEnum.DELIVERED.name());
        orderService.updateById(entity2);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
