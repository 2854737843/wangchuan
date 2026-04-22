package com.example.labcollab.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.entity.OrgMember;
import com.example.labcollab.exception.BadRequestException;
import com.example.labcollab.exception.ForbiddenException;
import com.example.labcollab.exception.UnauthorizedException;
import com.example.labcollab.mapper.OrgMemberMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OrgFilter extends OncePerRequestFilter {

    private final OrgMemberMapper orgMemberMapper;

    public OrgFilter(OrgMemberMapper orgMemberMapper) {
        this.orgMemberMapper = orgMemberMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (!requiresOrgContext(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("X-Org-Id");
            if (header == null || header.isBlank()) {
                throw new BadRequestException("Missing X-Org-Id");
            }

            Long orgId;
            try {
                orgId = Long.parseLong(header);
            } catch (NumberFormatException ex) {
                throw new BadRequestException("Invalid X-Org-Id");
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
                throw new UnauthorizedException("Unauthorized");
            }

            OrgMember member = orgMemberMapper.selectOne(new LambdaQueryWrapper<OrgMember>()
                    .eq(OrgMember::getOrgId, orgId)
                    .eq(OrgMember::getUserId, principal.userId()));
            if (member == null) {
                throw new ForbiddenException("Not a member of org");
            }

            OrgContext.setOrgId(orgId);
            filterChain.doFilter(request, response);
        } finally {
            OrgContext.clear();
        }
    }

    private boolean requiresOrgContext(String path) {
        return path.startsWith("/api/projects")
                || path.startsWith("/api/topics")
                || path.matches("^/api/orgs/\\d+/members.*");
    }
}
