package com.d2c.store.modules.order.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.PageModel;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.api.base.BaseCtrl;
import com.d2c.store.common.api.constant.PrefixConstant;
import com.d2c.store.common.sdk.fadada.FadadaClient;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.core.model.P2PDO;
import com.d2c.store.modules.core.service.P2PService;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.model.OrderItemDO;
import com.d2c.store.modules.order.query.OrderItemQuery;
import com.d2c.store.modules.order.query.OrderQuery;
import com.d2c.store.modules.order.service.OrderItemService;
import com.d2c.store.modules.security.model.UserDO;
import com.d2c.store.modules.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BaiCai
 */
@Api(description = "订单管理")
@RestController
@RequestMapping("/back/order")
public class OrderController extends BaseCtrl<OrderDO, OrderQuery> {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private P2PService p2PService;
    @Autowired
    private FadadaClient fadadaClient;

    @ApiOperation(value = "P2P查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<Page<OrderDO>> list(PageModel page, OrderQuery query) {
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        query.setP2pId(user.getP2pId());
        Page<OrderDO> pager = (Page<OrderDO>) service.page(page, QueryUtil.buildWrapper(query));
        List<String> orderSns = new ArrayList<>();
        Map<String, OrderDO> orderMap = new ConcurrentHashMap<>();
        for (OrderDO order : pager.getRecords()) {
            orderSns.add(order.getSn());
            orderMap.put(order.getSn(), order);
        }
        if (orderSns.size() == 0) return Response.restResult(pager, ResultCode.SUCCESS);
        OrderItemQuery itemQuery = new OrderItemQuery();
        itemQuery.setOrderSn(orderSns.toArray(new String[0]));
        List<OrderItemDO> orderItemList = orderItemService.list(QueryUtil.buildWrapper(itemQuery));
        for (OrderItemDO orderItem : orderItemList) {
            if (orderMap.get(orderItem.getOrderSn()) != null) {
                orderMap.get(orderItem.getOrderSn()).getOrderItemList().add(orderItem);
            }
        }
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "p2p签约")
    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public R p2pSign(Long id) {
        OrderDO orderDO = service.getById(id);
        Asserts.eq(orderDO.getStatus(), OrderDO.StatusEnum.WAIT_P2P_SIGN.name(), "订单状态不符");
        //p2p自动签章
        P2PDO p2PDO = p2PService.getById(orderDO.getP2pId());
        fadadaClient.extSignAuto(p2PDO.getCustomerId(), PrefixConstant.FDD_ORDER_C_TRANSATION_PREFIX + id, orderDO.getContractId(), p2PDO.getName() + "债权合同");
        //修改订单状态为客服待审核
        OrderDO order = new OrderDO();
        order.setId(id);
        order.setStatus(OrderDO.StatusEnum.WAIT_CUS_SIGN.name());
        service.updateById(order);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "合同归档(客服审核)")
    @RequestMapping(value = "/filling", method = RequestMethod.POST)
    public R filling(Long id) {
        OrderDO orderDO = service.getById(id);
        Asserts.eq(orderDO.getStatus(), OrderDO.StatusEnum.WAIT_CUS_SIGN.name(), "订单状态不符");
        //合同归档
        fadadaClient.contractFilling("C_" + orderDO.getSn());
        //修改订单状态为待发货
        OrderDO order = new OrderDO();
        order.setId(orderDO.getId());
        order.setStatus(OrderDO.StatusEnum.WAIT_DELIVER.name());
        service.updateById(order);
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
