package com.d2c.store.modules.product.query;

import com.d2c.store.common.api.annotation.Condition;
import com.d2c.store.common.api.base.BaseQuery;
import com.d2c.store.common.api.emuns.ConditionEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductQuery extends BaseQuery {

    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "状态 1,0")
    private Integer status;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "品类ID")
    private Long categoryId;
    @Condition(condition = ConditionEnum.IN, field = "category_id")
    @ApiModelProperty(value = "品类ID")
    private Long[] categoryIds;
    @Condition(condition = ConditionEnum.EQ)
    @ApiModelProperty(value = "名称")
    private String name;
    @Condition(condition = ConditionEnum.GE, field = "price")
    @ApiModelProperty(value = "最低价格")
    private BigDecimal priceL;
    @Condition(condition = ConditionEnum.LE, field = "price")
    @ApiModelProperty(value = "最高价格")
    private BigDecimal priceR;

}
