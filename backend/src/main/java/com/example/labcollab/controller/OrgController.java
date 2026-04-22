package com.example.labcollab.controller;

import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.Org;
import com.example.labcollab.entity.OrgMember;
import com.example.labcollab.mapper.OrgMapper;
import com.example.labcollab.mapper.OrgMemberMapper;
import com.example.labcollab.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orgs")
public class OrgController {

    private final OrgMapper orgMapper;
    private final OrgMemberMapper orgMemberMapper;

    public OrgController(OrgMapper orgMapper, OrgMemberMapper orgMemberMapper) {
        this.orgMapper = orgMapper;
        this.orgMemberMapper = orgMemberMapper;
    }

    @PostMapping
    public ApiResponse<Org> create(@Valid @RequestBody CreateOrgRequest request, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Org org = new Org();
        org.setName(request.name());
        org.setCreatedBy(principal.userId());
        orgMapper.insert(org);

        OrgMember owner = new OrgMember();
        owner.setOrgId(org.getId());
        owner.setUserId(principal.userId());
        owner.setRole("OWNER");
        orgMemberMapper.insert(owner);

        return ApiResponse.ok(org);
    }

    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> mine(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return ApiResponse.ok(orgMemberMapper.listMyOrgs(principal.userId()));
    }

    public record CreateOrgRequest(@NotBlank String name) {
    }
}
