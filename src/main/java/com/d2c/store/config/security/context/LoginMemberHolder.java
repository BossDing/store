package com.d2c.store.config.security.context;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.config.security.constant.SecurityConstant;
import com.d2c.store.modules.member.model.MemberDO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cai
 */
@Controller
public class LoginMemberHolder {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RedisTemplate redisTemplate;

    public MemberDO getLoginMember() {
        String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
        // Token信息不存在，登录过期
        if (StrUtil.isBlank(accessToken)) throw new ApiException(ResultCode.LOGIN_EXPIRED);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            // 解析得到账号和平台ID
            String account = claims.getSubject();
            String p2pId = claims.get(SecurityConstant.AUTHORITIES).toString();
            // 获取登录信息
            MemberDO member = (MemberDO) redisTemplate.opsForValue().get("MEMBER::session:" + account);
            // 登录信息不存在，登录过期
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, member);
            // 平台ID不符合，无权访问
            Asserts.eq(member.getAccountInfo().getP2pId(), p2pId, ResultCode.ACCESS_DENIED);
            // Token信息已经变更，登录过期
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, member.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            return member;
        } catch (Exception e) {
            throw new ApiException(ResultCode.LOGIN_EXPIRED);
        }
    }

    public Long getLoginId() {
        return this.getLoginMember().getId();
    }

    public String getLoginAccount() {
        return this.getLoginMember().getAccount();
    }

    public Long getP2pId() {
        return this.getLoginMember().getAccountInfo().getP2pId();
    }

}
