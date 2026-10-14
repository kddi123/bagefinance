package com.bage.finance.biz.service.impl;

import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.AccountBook;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetAccountBookVo;
import com.bage.finance.biz.dto.vo.ListAccountBookVo;
import com.bage.finance.biz.mapper.AccountBookMapper;
import com.bage.finance.biz.service.AccountBookService;
import com.bage.mybatis.help.Criteria;
import com.bage.mybatis.help.MyBatisWrapper;
import com.bage.mybatis.help.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import static com.bage.finance.biz.domain.AccountBookField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountBookServiceImpl implements AccountBookService {
    final AccountBookMapper accountBookMapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;

    /**
     * 查询账套明细
     *
     * @param id
     * @return
     */
    @Override
    public GetAccountBookVo get(long id) {
        MyBatisWrapper<AccountBook> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(Id, CompanyName, UnifiedSocialCreditCode, IndustryId, ValueAddedTaxCate, EnableVoucherVerify,
                StartTime, AccountingStandard, EnableFixedAssets, EnableCapital, EnablePsi);
        myBatisWrapper.whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        AccountBook accountBook = accountBookMapper.get(myBatisWrapper);
        return objectConvertor.toGetAccountBookVo(accountBook);
    }

    /**
     * 查询账套列表
     *
     * @param request
     * @return
     */
    @Override
    public PageInfo<ListAccountBookVo> list(ListAccountBookForm request) {
        MyBatisWrapper<AccountBook> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(Id, CompanyName, ValueAddedTaxCate, AccountingStandard, CreateTime, StartTime,
                EnableVoucherVerify, Disable)
                .page(request.getPageNum(), request.getPageSize());
        Criteria where = myBatisWrapper.whereBuilder().andEq(setDelFlag(false));
        if (Strings.isNotBlank(request.getCompanyName())) {
            where.andLike(setCompanyName("%" + request.getCompanyName() + "%"));
        }
        if (request.getDisable() != null) {
            where.andEq(setDisable(request.getDisable()));
        }
        myBatisWrapper.and().andEq(TenantId, tokenService.getThreadLocalTenantId());
        myBatisWrapper.orderByDesc(CreateTime);
        PageInfo<AccountBook> accountBookPageInfo = myBatisWrapper.listPage(accountBookMapper);
        return objectConvertor.toListAccountBookVoPage(accountBookPageInfo);
    }

    /**
     * 创建账套
     *
     * @param request
     * @return
     */
    @Override
    public boolean add(AddAccountBookForm request) {
        AccountBook accountBook = objectConvertor.toAccountBook(request);
        accountBook.setMemberId(tokenService.getThreadLocalUserId());
        accountBook.setTenantId(tokenService.getThreadLocalTenantId());
        accountBook.initDefault();
        return accountBookMapper.insert(accountBook) > 0;
    }

    /**
     * 禁用启用账套
     *
     * @param form
     * @return 结果
     */
    @Override
    public boolean disable(AccountBookDisableForm form) {
        MyBatisWrapper<AccountBook> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.update(setDisable(form.getDisable()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(Disable, !form.getDisable())
                .andEq(TenantId, tokenService.getThreadLocalTenantId());

        return accountBookMapper.updateField(myBatisWrapper) > 0;
    }

    /**
     * 删除账套
     *
     * @param form 账套
     * @return 结果
     */
    @Override
    public boolean del(DelForm form) {
        MyBatisWrapper<AccountBook> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.update(setDelFlag(true))
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return accountBookMapper.updateField(myBatisWrapper) > 0;
    }

    /**
     * 编辑账套
     *
     * @param form
     * @return
     */
    @Override
    public boolean update(UpdateAccountBookForm form) {
        MyBatisWrapper<AccountBook> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.update(setCompanyName(form.getCompanyName()))
                .update(setIndustryId(form.getIndustryId()))
                .update(setValueAddedTaxCate(form.getValueAddedTaxCate()))
                .update(setEnableCapital(form.getEnableCapital()))
                .update(setEnableFixedAssets(form.getEnableFixedAssets()))
                .update(setEnablePsi(form.getEnablePsi()))
                .update(setEnableVoucherVerify(form.getEnableVoucherVerify()))
                .whereBuilder()
                .andEq(setId(form.getId()))
                .andEq(TenantId, tokenService.getThreadLocalTenantId());

        return accountBookMapper.updateField(myBatisWrapper) > 0;
    }
}
