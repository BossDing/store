package com.d2c.store.config.security.jwtfilter;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.api.Response;
import com.d2c.store.common.api.ResultCode;
import com.d2c.store.common.utils.SpringUtil;
import com.d2c.store.config.security.authentication.SecurityUserDetails;
import com.d2c.store.config.security.constant.SecurityConstant;
import com.d2c.store.modules.security.model.UserDO;
import com.d2c.store.modules.security.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author BaiCai
 */
@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private static final String NEED_FILTER_PATH_PREFIX = "/back";

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getRequestURI().startsWith(NEED_FILTER_PATH_PREFIX)) {
            String accessToken = request.getHeader(SecurityConstant.ACCESS_TOKEN);
            if (StrUtil.isNotBlank(accessToken)) {
                UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken, response);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String accessToken, HttpServletResponse response) {
        try {
            // JWT解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                    .parseClaimsJws(accessToken.replace(SecurityConstant.TOKEN_PREFIX, ""))
                    .getBody();
            // 解析得到账号
            String username = claims.getSubject();
            // 获取登录信息
            UserDO user = SpringUtil.getBean(UserService.class).findByUsername(username);
            // 登录信息不存在，登录过期
            Asserts.notNull(ResultCode.LOGIN_EXPIRED, user);
            // Token信息已经变更，登录过期
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(accessToken, user.getAccessToken())) {
                throw new ApiException(ResultCode.LOGIN_EXPIRED);
            }
            // 刷新本地登录信息
            SecurityUserDetails securityUserDetail = new SecurityUserDetails(user);
            User principal = new User(username, "", securityUserDetail.getAuthorities());
            return new UsernamePasswordAuthenticationToken(principal, null, securityUserDetail.getAuthorities());
        } catch (ExpiredJwtException e) {
            Response.failed(ResultCode.LOGIN_EXPIRED);
        } catch (JwtException e) {
            log.error(e.getMessage(), e);
            Response.failed(ResultCode.LOGIN_EXPIRED, "accessToken解析错误");
        }
        return null;
    }

}
