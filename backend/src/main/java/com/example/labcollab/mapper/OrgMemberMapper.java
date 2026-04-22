package com.example.labcollab.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.labcollab.entity.OrgMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface OrgMemberMapper extends BaseMapper<OrgMember> {

    @Select("""
            select o.id, o.name, om.role
            from org_member om
            join org o on o.id = om.org_id
            where om.user_id = #{userId}
            order by o.id desc
            """)
    List<Map<String, Object>> listMyOrgs(@Param("userId") Long userId);

    @Select("""
            select om.org_id, om.user_id, su.username, om.role
            from org_member om
            join sys_user su on su.id = om.user_id
            where om.org_id = #{orgId}
            order by om.id asc
            """)
    List<Map<String, Object>> listMembers(@Param("orgId") Long orgId);
}
