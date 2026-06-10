package com.online.mall.config;

import com.online.mall.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        
        // 获取token
        String token = getTokenFromRequest(request);
        
        if (token != null && jwtTokenUtil.validateToken(token)) {
            // 从token中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenUtil.validateToken(token)) {
                    // 创建认证token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // 将用户ID设置到request属性中，方便后续使用
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);
                    
                    log.debug("用户认证成功: username={}, userId={}", username, userId);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtTokenUtil.getHeader());

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            // 验证是否为有效的JWT格式（包含两个点）
            if (isValidJwtFormat(token)) {
                return token;
            }
            log.warn("无效的JWT格式: {}", token);
        }

        // 从参数中获取token（兼容性）
        String token = request.getParameter("token");
        if (token != null && !token.isEmpty() && isValidJwtFormat(token)) {
            return token;
        }

        return null;
    }

    /**
     * 检查是否为有效的JWT格式
     */
    private boolean isValidJwtFormat(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        // JWT格式应该包含两个点（三部分：header.payload.signature）
        int count = 0;
        for (char c : token.toCharArray()) {
            if (c == '.') count++;
        }
        return count == 2;
    }
    
    /**
     * 排除不需要认证的路径
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // 不需要认证的路径（context-path已去除）
        return path.startsWith("/user/login") ||
               path.startsWith("/user/register") ||
               path.startsWith("/user/check") ||
               path.startsWith("/user/password/reset") ||
               path.equals("/user/health") ||
               path.startsWith("/product") ||
               path.startsWith("/category") ||
               path.startsWith("/region") ||
               path.startsWith("/images") ||
               path.startsWith("/uploads") ||
               path.startsWith("/hiking") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/webjars") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/error");
    }
}