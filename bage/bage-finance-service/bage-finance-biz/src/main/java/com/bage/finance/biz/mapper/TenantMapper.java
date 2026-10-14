package com.bage.finance.biz.mapper;

import com.bage.finance.biz.domain.Tenant;
import com.bage.mybatis.help.CommonMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TenantMapper extends CommonMapper<Tenant> {
}