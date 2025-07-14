package acc.firewatch.config.jwt;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static acc.firewatch.common.response.dto.CustomResponseUtils.writeErrorResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api") ||
                path.startsWith("/h2") ||
                path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        // 토큰 유무 확인 및 형식 확인
        if (bearerToken == null) {
            writeErrorResponse(response, ErrorCode.NOT_FOUND_AUTHORIZATION_HEADER);
            return;
        }

        if (!bearerToken.startsWith("Bearer ")) {
            writeErrorResponse(response, ErrorCode.NULL_POINT_HEADER_REQUEST);
            return;
        }

        String token = bearerToken.substring(7);


        try{
            // 토큰 유효성 검사, claim 추출
            jwtTokenProvider.validateToken(token);

            Claims claims = jwtTokenProvider.parseClaims(token);

            // 사용자 인증 객체 생성
            String phoneNum = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            // Authentication 객체 생성 후 등록
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            phoneNum,         // 전화번호
                            claims,           // memberId
                            authorities       // 권한
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (CustomException e) {
            writeErrorResponse(response, e.getErrorCode());
            return;
        }

        filterChain.doFilter(request, response);
    }
}