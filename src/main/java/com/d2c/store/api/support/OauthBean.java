package com.d2c.store.api.support;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Cai
 */
@Data
@ApiModel(description = "授权请求POJO")
public class OauthBean implements Serializable {

    @ApiModelProperty(value = "平台ID")
    private String appId;
    @ApiModelProperty(value = "授权账号")
    private String mobile;
    @ApiModelProperty(value = "真实姓名")
    private String name;
    @ApiModelProperty(value = "身份证号")
    private String identity;
    @ApiModelProperty(value = "授权金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "请求签名")
    private String sign;

}
