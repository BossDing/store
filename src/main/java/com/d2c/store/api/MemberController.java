package com.d2c.store.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.d2c.store.api.base.BaseController;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.modules.member.model.MemberDO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cai
 */
@Api(description = "会员业务")
@RestController
@RequestMapping("/api/member")
public class MemberController extends BaseController {

    @ApiOperation(value = "登录信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public R<MemberDO> info() {
        return Response.restResult(loginMemberHolder.getLoginMember(), ResultCode.SUCCESS);
    }

}
