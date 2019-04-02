package com.d2c.store.modules.order.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.d2c.store.common.api.annotation.Assert;
import com.d2c.store.common.api.annotation.Prevent;
import com.d2c.store.common.api.base.extension.BaseDelDO;
import com.d2c.store.common.api.emuns.AssertEnum;
import com.d2c.store.modules.member.model.support.IAddress;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("O_ORDER")
@ApiModel(description = "订单表")
public class OrderDO extends BaseDelDO implements IAddress {

    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "平台ID")
    private Long p2pId;
    @Prevent
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员ID")
    private Long memberId;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;
    @ApiModelProperty(value = "省份")
    private String province;
    @ApiModelProperty(value = "城市")
    private String city;
    @ApiModelProperty(value = "区县")
    private String district;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "手机")
    private String mobile;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "订单号")
    private String sn;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "类型")
    private String type;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "状态")
    private String status;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "商品总价")
    private BigDecimal productAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "运费价格")
    private BigDecimal freightAmount;
    @Assert(type = AssertEnum.NOT_NULL)
    @ApiModelProperty(value = "实际支付")
    private BigDecimal payAmount;
    @TableField(exist = false)
    @ApiModelProperty(value = "类型名")
    private String typeName;
    @TableField(exist = false)
    @ApiModelProperty(value = "状态名")
    private String statusName;
    @TableField(exist = false)
    @ApiModelProperty(value = "订单明细列表")
    private List<OrderItemDO> orderItemList = new ArrayList<>();

    public String getTypeName() {
        if (StrUtil.isBlank(type)) return "";
        return TypeEnum.valueOf(type).getDescription();
    }

    public String getStatusName() {
        if (StrUtil.isBlank(status)) return "";
        return StatusEnum.valueOf(status).getDescription();
    }

    public int getExpireMinute() {
        return TypeEnum.NORMAL.expireMinutes;
    }

    public enum TypeEnum {
        //
        NORMAL("普通", 120);
        //
        private String description;
        private Integer expireMinutes;

        TypeEnum(String description, Integer expired) {
            this.description = description;
            this.expireMinutes = expired;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public enum StatusEnum {
        //
        WAIT_MEM_SIGN("待用户签约"),
        WAIT_P2P_SIGN("待P2P审核"),
        WAIT_CUS_SIGN("待客服审核"),
        SUCCESS("交易成功"), CLOSED("交易关闭");
        //
        private String description;

        StatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
