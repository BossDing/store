package com.d2c.store.config.security.context;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.utils.SpringUtil;
import com.d2c.store.config.security.constant.SecurityConstant;
import com.d2c.store.modules.member.model.MemberDO;
import com.d2c.store.modules.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
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

    public MemberDO getLoginMember() {
        String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
        if (StrUtil.isBlank(accessToken)) throw new ApiException(ResultCode.LOGIN_EXPIRED);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            String account = claims.getSubject();
            MemberDO member = SpringUtil.getBean(MemberService.class).findByAccount(account);
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, member);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, member.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            return member;
        } catch (JwtException e) {
            throw new ApiException(ResultCode.LOGIN_EXPIRED);
        }
    }

    public Long getLoginId() {
        return this.getLoginMember().getId();
    }

    public String getLoginAccount() {
        return this.getLoginMember().getAccount();
    }

}
