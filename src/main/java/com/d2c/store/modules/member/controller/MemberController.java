package com.d2c.store.modules.member.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.store.common.api.PageModel;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.api.base.BaseCtrl;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.member.model.AccountDO;
import com.d2c.store.modules.member.model.MemberDO;
import com.d2c.store.modules.member.query.AccountQuery;
import com.d2c.store.modules.member.query.MemberQuery;
import com.d2c.store.modules.member.service.MemberService;
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
@Api(description = "会员管理")
@RestController
@RequestMapping("/back/member")
public class MemberController extends BaseCtrl<MemberDO, MemberQuery> {

    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;

    @ApiOperation(value = "P2P查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public R<Page<MemberDO>> list(PageModel page, AccountQuery query) {
        UserDO user = userService.findByUsername(loginUserHolder.getUsername());
        query.setP2pId(user.getP2pId());
        Page<AccountDO> pager = (Page<AccountDO>) service.page(page, QueryUtil.buildWrapper(query));
        List<Long> memberIds = new ArrayList<>();
        Map<Long, AccountDO> accountMap = new ConcurrentHashMap<>();
        for (AccountDO account : pager.getRecords()) {
            memberIds.add(account.getMemberId());
            accountMap.put(account.getMemberId(), account);
        }
        if (memberIds.size() == 0) return Response.restResult(new Page<MemberDO>(), ResultCode.SUCCESS);
        List<MemberDO> memberList = (List<MemberDO>) memberService.listByIds(memberIds);
        for (MemberDO member : memberList) {
            if (accountMap.get(member.getId()) != null) {
                member.setAccountInfo(accountMap.get(member.getId()));
            }
        }
        Page<MemberDO> memberPager = new Page<>();
        BeanUtil.copyProperties(pager, memberPager, "records", "optimizeCountSql");
        memberPager.setRecords(memberList);
        return Response.restResult(memberPager, ResultCode.SUCCESS);
    }

}
