package com.codegym.ecommercemanage.service.jwt;

import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.model.UserPrincipal;
import com.codegym.ecommercemanage.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws IOException, ServletException {

        // 🚑 1. BỎ QUA CÁC URL KHÔNG CẦN CHECK
        String uri = req.getRequestURI();
        if (uri.startsWith("/login") || uri.startsWith("/register") || "OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        // 🔍 DEBUG: In ra xem Frontend gửi cái gì lên
        // System.out.println("🔍 [Filter] Header nhận được: " + header);

        try {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                // Bước 1: Giải mã Token
                String username = jwtService.extractUserName(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Bước 2: Tìm User trong DB
                    User user = userRepo.findByUsername(username);

                    if (user != null) {
                        // 🔍 DEBUG QUAN TRỌNG: Kiểm tra xem Role có load được không
                        // Nếu dòng này in ra lỗi -> Do chưa có FetchType.EAGER
                        // System.out.println("🔍 [Filter] Tìm thấy User: " + user.getUsername() + " | Roles: " + user.getRoles());

                        UserPrincipal principal = UserPrincipal.build(user);

                        // 🔍 DEBUG QUAN TRỌNG: Kiểm tra quyền cuối cùng nạp vào Security
                        System.out.println("✅ [Filter] Cấp quyền cho User: " + principal.getAuthorities());

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        principal, null, principal.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        System.err.println("❌ [Filter] Token hợp lệ nhưng không tìm thấy User trong DB: " + username);
                    }
                }
            }
        } catch (Exception e) {
            // 🔥 BẮT LỖI TẠI ĐÂY: Nếu token sai hoặc lỗi code, nó sẽ hiện ra thay vì âm thầm trả về 403
            System.err.println("❌ [Filter] LỖI XÁC THỰC: " + e.getMessage());
            e.printStackTrace();
        }

        chain.doFilter(req, res);
    }

}
