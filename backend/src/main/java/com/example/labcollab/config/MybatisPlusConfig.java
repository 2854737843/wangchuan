package com.example.labcollab.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.example.labcollab.security.OrgContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Long orgId = OrgContext.getOrgId();
                return new LongValue(orgId == null ? -1L : orgId);
            }

            @Override
            public String getTenantIdColumn() {
                return "org_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return "sys_user".equalsIgnoreCase(tableName)
                        || "org".equalsIgnoreCase(tableName)
                        || "org_member".equalsIgnoreCase(tableName)
                        || "flyway_schema_history".equalsIgnoreCase(tableName);
            }
        }));
        return interceptor;
    }
}
