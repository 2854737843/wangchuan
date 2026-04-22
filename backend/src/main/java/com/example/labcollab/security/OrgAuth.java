package com.example.labcollab.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.entity.OrgMember;
import com.example.labcollab.exception.ForbiddenException;
import com.example.labcollab.exception.OrgIdMismatchException;
import com.example.labcollab.mapper.OrgMemberMapper;
import org.springframework.stereotype.Component;

@Component
public class OrgAuth {

    private final OrgMemberMapper orgMemberMapper;

    public OrgAuth(OrgMemberMapper orgMemberMapper) {
        this.orgMemberMapper = orgMemberMapper;
    }

    public void assertOrgIdMatchPath(Long pathOrgId) {
        Long orgId = OrgContext.getOrgId();
        if (orgId == null || !orgId.equals(pathOrgId)) {
            throw new OrgIdMismatchException();
        }
    }

    public void assertCanManageMembers(Long orgId, Long userId) {
        OrgMember member = orgMemberMapper.selectOne(new LambdaQueryWrapper<OrgMember>()
                .eq(OrgMember::getOrgId, orgId)
                .eq(OrgMember::getUserId, userId));
        if (member == null) {
            throw new ForbiddenException("Not a member of org");
        }
        if (!("OWNER".equals(member.getRole()) || "TEACHER".equals(member.getRole()))) {
            throw new ForbiddenException("No permission");
        }
    }
}
