package com.example.labcollab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.OrgMember;
import com.example.labcollab.entity.SysUser;
import com.example.labcollab.exception.BadRequestException;
import com.example.labcollab.exception.NotFoundException;
import com.example.labcollab.mapper.OrgMemberMapper;
import com.example.labcollab.mapper.SysUserMapper;
import com.example.labcollab.security.OrgAuth;
import com.example.labcollab.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orgs/{orgId}/members")
public class OrgMemberController {

    private final OrgAuth orgAuth;
    private final OrgMemberMapper orgMemberMapper;
    private final SysUserMapper sysUserMapper;

    public OrgMemberController(OrgAuth orgAuth, OrgMemberMapper orgMemberMapper, SysUserMapper sysUserMapper) {
        this.orgAuth = orgAuth;
        this.orgMemberMapper = orgMemberMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@PathVariable Long orgId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        orgAuth.assertOrgIdMatchPath(orgId);
        orgAuth.assertCanManageMembers(orgId, principal.userId());
        return ApiResponse.ok(orgMemberMapper.listMembers(orgId));
    }

    @PostMapping
    public ApiResponse<Void> add(@PathVariable Long orgId, @Valid @RequestBody AddMemberRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        orgAuth.assertOrgIdMatchPath(orgId);
        orgAuth.assertCanManageMembers(orgId, principal.userId());

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.username()));
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        OrgMember exists = orgMemberMapper.selectOne(new LambdaQueryWrapper<OrgMember>()
                .eq(OrgMember::getOrgId, orgId)
                .eq(OrgMember::getUserId, user.getId()));
        if (exists != null) {
            throw new BadRequestException("User already in org");
        }

        OrgMember member = new OrgMember();
        member.setOrgId(orgId);
        member.setUserId(user.getId());
        member.setRole(request.role());
        orgMemberMapper.insert(member);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> remove(@PathVariable Long orgId, @PathVariable Long userId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        orgAuth.assertOrgIdMatchPath(orgId);
        orgAuth.assertCanManageMembers(orgId, principal.userId());

        int affected = orgMemberMapper.delete(new LambdaUpdateWrapper<OrgMember>()
                .eq(OrgMember::getOrgId, orgId)
                .eq(OrgMember::getUserId, userId));
        if (affected == 0) {
            throw new NotFoundException("Member not found");
        }
        return ApiResponse.ok();
    }

    public record AddMemberRequest(
            @NotBlank String username,
            @NotBlank @Pattern(regexp = "OWNER|TEACHER|STUDENT", message = "role must be OWNER/TEACHER/STUDENT") String role
    ) {
    }
}
