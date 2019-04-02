package com.d2c.store.modules.core.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.store.common.api.annotation.Assert;
import com.d2c.store.common.api.base.extension.BaseDelDO;
import com.d2c.store.common.api.emuns.AssertEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Cai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("CORE_P2P")
@ApiModel(description = "P2P平台表")
public class P2PDO extends BaseDelDO {

    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "密钥")
    private String secret;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "联系方式")
    private String mobile;
    @ApiModelProperty(value = "首页banner")
    private String banner;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "销售金额")
    private BigDecimal salesAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "最低消费金额")
    private BigDecimal minAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "允许偏差金额")
    private BigDecimal diffAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "授权有效期-小时")
    private Integer oauthTime;

}
