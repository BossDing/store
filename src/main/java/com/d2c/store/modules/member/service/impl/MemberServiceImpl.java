package com.d2c.store.modules.member.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.d2c.store.api.support.OauthBean;
import com.d2c.store.common.api.base.BaseService;
import com.d2c.store.common.utils.QueryUtil;
import com.d2c.store.modules.core.model.P2PDO;
import com.d2c.store.modules.member.mapper.MemberMapper;
import com.d2c.store.modules.member.model.AccountDO;
import com.d2c.store.modules.member.model.MemberDO;
import com.d2c.store.modules.member.query.AccountQuery;
import com.d2c.store.modules.member.query.MemberQuery;
import com.d2c.store.modules.member.service.AccountService;
import com.d2c.store.modules.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiCai
 */
@Service
public class MemberServiceImpl extends BaseService<MemberMapper, MemberDO> implements MemberService {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    @Cacheable(value = "MEMBER", key = "'session:'+#oauthBean.mobile", unless = "#result == null")
    public MemberDO doOauth(OauthBean oauthBean, P2PDO p2pDO, String loginIp) {
        MemberQuery mq = new MemberQuery();
        mq.setAccount(oauthBean.getMobile());
        MemberDO member = this.getOne(QueryUtil.buildWrapper(mq));
        if (member != null) {
            // 原已注册会员
            AccountQuery aq = new AccountQuery();
            aq.setMemberId(member.getId());
            aq.setP2pId(p2pDO.getId());
            AccountDO accountDO = accountService.getOne(QueryUtil.buildWrapper(aq));
            if (accountDO != null) {
                // 覆盖授权额度
                member.setAccountInfo(this.coverAccount(accountDO, oauthBean.getAmount(), p2pDO.getOauthTime()));
            } else {
                // 给予授权额度
                member.setAccountInfo(this.renderAccount(member.getId(), p2pDO.getId(), oauthBean.getAmount(), p2pDO.getOauthTime()));
            }
            return member;
        } else {
            // 原未注册会员
            MemberDO entity = new MemberDO();
            entity.setAccount(oauthBean.getMobile());
            entity.setPassword(new BCryptPasswordEncoder().encode(oauthBean.getMobile().substring(oauthBean.getMobile().length() - 6)));
            entity.setNickname(oauthBean.getName());
            entity.setIdentity(oauthBean.getIdentity());
            entity.setRegisterIp(loginIp);
            entity.setStatus(1);
            super.save(entity);
            // 给予授权额度
            entity.setAccountInfo(this.renderAccount(entity.getId(), p2pDO.getId(), oauthBean.getAmount(), p2pDO.getOauthTime()));
            return entity;
        }
    }

    // 给予授权额度
    private AccountDO renderAccount(Long memberId, Long p2pId, BigDecimal amount, Integer hours) {
        AccountDO entity = new AccountDO();
        entity.setMemberId(memberId);
        entity.setP2pId(p2pId);
        entity.setAmount(amount);
        entity.setDeadline(DateUtil.offsetHour(new Date(), hours).toJdkDate());
        accountService.save(entity);
        return entity;
    }

    // 覆盖授权额度
    private AccountDO coverAccount(AccountDO old, BigDecimal amount, Integer hours) {
        Date now = new Date();
        if (old.getDeadline().after(now)) {
            AccountDO entity = new AccountDO();
            entity.setId(old.getId());
            entity.setAmount(amount);
            entity.setDeadline(DateUtil.offsetHour(now, hours).toJdkDate());
            accountService.updateById(entity);
            old.setAmount(amount);
            old.setDeadline(entity.getDeadline());
            old.setModifyDate(now);
            return old;
        }
        return old;
    }

    @Override
    @Transactional
    @CachePut(value = "MEMBER", key = "'session:'+#member.account", unless = "#result == null")
    public MemberDO doLogin(MemberDO member, String loginIp, String accessToken, Date accessExpired) {
        MemberDO entity = new MemberDO();
        entity.setAccessToken(new BCryptPasswordEncoder().encode(accessToken));
        entity.setAccessExpired(accessExpired);
        entity.setLoginDate(new Date());
        entity.setLoginIp(loginIp);
        MemberQuery query = new MemberQuery();
        query.setAccount(member.getAccount());
        QueryWrapper wrapper = QueryUtil.buildWrapper(query);
        this.update(entity, wrapper);
        member.setAccessExpired(accessExpired);
        member.setLoginDate(new Date());
        member.setLoginIp(loginIp);
        return member;
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public boolean doLogout(String account) {
        MemberDO entity = new MemberDO();
        entity.setAccessToken("");
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    @CacheEvict(value = "MEMBER", key = "'session:'+#account")
    public boolean updatePassword(String account, String password) {
        MemberDO entity = new MemberDO();
        entity.setPassword(password);
        MemberQuery query = new MemberQuery();
        query.setAccount(account);
        return this.update(entity, QueryUtil.buildWrapper(query));
    }

    @Override
    public boolean updateById(MemberDO entity) {
        MemberDO member = memberMapper.selectById(entity.getId());
        redisTemplate.delete("MEMBER::session:" + member.getAccount());
        return super.updateById(entity);
    }

}
