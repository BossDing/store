package com.d2c.store.modules.core.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.PageModel;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.api.base.BaseCtrl;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.core.model.P2PDO;
import com.d2c.store.modules.core.query.P2PQuery;
import com.d2c.store.modules.security.model.UserDO;
import com.d2c.store.modules.security.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiCai
 */
@Api(description = "P2P平台管理")
@RestController
@RequestMapping("/back/p2p")
public class P2PController extends BaseCtrl<P2PDO, P2PQuery> {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "P2P查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<Page<P2PDO>> list(PageModel page, P2PQuery query) {
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        query.setId(user.getP2pId());
        Page<P2PDO> pager = (Page<P2PDO>) service.page(page, QueryUtil.buildWrapper(query));
        return Response.restResult(pager, ResultCode.SUCCESS);
    }

    @ApiOperation(value = "P2P更新数据")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R<P2PDO> save(@RequestBody P2PDO entity) {
        Asserts.notNull(ResultCode.REQUEST_PARAM_NULL, entity);
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        entity.setId(user.getP2pId());
        service.updateById(entity);
        return Response.restResult(service.getById(entity.getId()), ResultCode.SUCCESS);
    }

}
