package com.d2c.store.api.callback;

import com.baomidou.mybatisplus.extension.api.R;
import com.d2c.store.api.base.BaseController;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.query.OrderQuery;
import com.d2c.store.modules.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author Cai
 */
@Api(description = "第三方回调业务")
@RestController
@RequestMapping("/api/callback")
public class C_CallbackController extends BaseController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "法大大回调接口")
    @RequestMapping(value = "/fadada", method = RequestMethod.POST)
    public R fadada(String transaction_id, String contract_id, String result_code, String result_desc, String timestamp, String msg_digest) {
        Asserts.eq(result_code, "3000", "签约失败，合同编号：" + contract_id);
        OrderQuery oq = new OrderQuery();
        oq.setContractId(contract_id);
        OrderDO orderDO = orderService.getOne(QueryUtil.buildWrapper(oq));
        if (OrderDO.StatusEnum.WAIT_MEM_SIGN.name().equals(orderDO.getStatus())) {
            OrderDO order = new OrderDO();
            order.setId(orderDO.getId());
            order.setStatus(OrderDO.StatusEnum.WAIT_P2P_SIGN.name());
            order.setMemSignDate(new Date());
            orderService.updateById(order);
        }
        return Response.restResult(null, ResultCode.SUCCESS);
    }

}
