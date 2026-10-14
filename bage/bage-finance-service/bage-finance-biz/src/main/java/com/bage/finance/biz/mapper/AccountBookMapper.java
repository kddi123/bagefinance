package com.bage.finance.biz.mapper;

import com.bage.finance.biz.domain.AccountBook;
import com.bage.mybatis.help.CommonMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountBookMapper extends CommonMapper<AccountBook> {
}