package com.d2c.store.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.query.OrderQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author BaiCai
 */
public interface OrderMapper extends BaseMapper<OrderDO> {

    Map<String, Object> countDaily(@Param("query") OrderQuery query);

}
