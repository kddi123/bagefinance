package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.AssistCalculateSupplier;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateAssistCalculateSupplierForm;
import com.bage.finance.biz.dto.form.UpdateAssistCalculateSupplierForm;
import com.bage.finance.biz.dto.vo.GetAssistCalculateSupplierVo;
import com.bage.finance.biz.dto.vo.ListAssistCalculateSupplierVo;
import com.bage.finance.biz.enums.AssistCalculateCateCodeEnum;
import com.bage.finance.biz.mapper.AssistCalculateSupplierMapper;
import com.bage.finance.biz.service.AssistCalculateHandleService;
import com.bage.finance.biz.service.SysRegionService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

import static com.bage.finance.biz.domain.AssistCalculateSupplierField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistCalculateSupplierServiceImpl implements AssistCalculateHandleService {
    final AssistCalculateSupplierMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final SysRegionService sysRegionService;

    @Override
    public AssistCalculateCateCodeEnum getAssistCalculateCateCodeEnum() {
        return AssistCalculateCateCodeEnum.SUPPLIER;
    }

    /**
     * 添加供应商辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean create(Object obj) {
        CreateAssistCalculateSupplierForm form = (CreateAssistCalculateSupplierForm) obj;
        //检测地区编码是否合法
        if (Strings.isNotBlank(form.getCountyCode())) {
            sysRegionService.checkRegionCode(form.getProvinceCode(), form.getCityCode(), form.getCountyCode());
        }
        AssistCalculateSupplier assistCalculateSupplier = objectConvertor.toAssistCalculateSupplier(form);
        assistCalculateSupplier.initDefault();
        assistCalculateSupplier.setMemberId(tokenService.getThreadLocalUserId());
        assistCalculateSupplier.setUpdateMemberId(tokenService.getThreadLocalUserId());
        assistCalculateSupplier.setTenantId(tokenService.getThreadLocalTenantId());

        return mapper.insert(assistCalculateSupplier) > 0;
    }

    /**
     * 查询辅助核算供应商列表
     *
     * @return
     */
    @Override
    public List<ListAssistCalculateSupplierVo> listByAssistCalculateSummaryIds(List<Long> assistCalculateSummaryIds) {
        MyBatisWrapper<AssistCalculateSupplier> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, SupplierCate, Address, Contacts, Phone, UnifiedSocialCreditCode, Disable,
                AssistCalculateSummaryId)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andIn(AssistCalculateSummaryId, assistCalculateSummaryIds);
        List<AssistCalculateSupplier> assistCalculates = mapper.list(wrapper);
        return objectConvertor.toListAssistCalculateSupplierVo(assistCalculates);
    }

    /**
     * 修改客户辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean update(Object obj) {
        UpdateAssistCalculateSupplierForm form = (UpdateAssistCalculateSupplierForm) obj;
        //检测地区编码是否合法
        if (Strings.isNotBlank(form.getCountyCode())) {
            sysRegionService.checkRegionCode(form.getProvinceCode(), form.getCityCode(), form.getCountyCode());
        }
        MyBatisWrapper<AssistCalculateSupplier> wrapper = new MyBatisWrapper<>();
        wrapper.update(UpdateTime, new Date())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UnifiedSocialCreditCode, form.getUnifiedSocialCreditCode())
                .update(SupplierCate, form.getSupplierCate())
                .update(ProvinceCode, form.getProvinceCode())
                .update(CityCode, form.getCityCode())
                .update(CountyCode, form.getCountyCode())
                .update(Address, form.getAddress())
                .update(Contacts, form.getContacts())
                .update(Phone, form.getPhone())
                .whereBuilder()
                .andEq(AssistCalculateSummaryId, form.getId())
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false);
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("修改失败");
        }
        return true;
    }

    /**
     * 获取客户辅助核算详情
     *
     * @param id
     * @return
     */
    @Override
    public GetAssistCalculateSupplierVo get(long id) {
        AssistCalculateSupplier assistCalculateSupplier = getById(id);
        GetAssistCalculateSupplierVo result = objectConvertor.toGetAssistCalculateSupplierVo(assistCalculateSupplier);
        return result;
    }

    /**
     * 查询客户辅助核算明细
     *
     * @param id
     * @return
     */
    private AssistCalculateSupplier getById(long id) {
        MyBatisWrapper<AssistCalculateSupplier> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, SupplierCate, UnifiedSocialCreditCode, ProvinceCode, CityCode, CountyCode, Address, Contacts,
                Phone, ProvinceCode, CityCode, CountyCode)
                .whereBuilder()
                .andEq(AssistCalculateSummaryId, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.get(wrapper);
    }
}
