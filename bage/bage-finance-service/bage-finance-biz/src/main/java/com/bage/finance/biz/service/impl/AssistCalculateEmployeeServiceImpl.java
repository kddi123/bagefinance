package com.bage.finance.biz.service.impl;

import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.AssistCalculateEmployee;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.CreateAssistCalculateEmployeeForm;
import com.bage.finance.biz.dto.form.UpdateAssistCalculateEmployeeForm;
import com.bage.finance.biz.dto.vo.GetAssistCalculateEmployeeVo;
import com.bage.finance.biz.dto.vo.ListAssistCalculateEmployeeVo;
import com.bage.finance.biz.enums.AssistCalculateCateCodeEnum;
import com.bage.finance.biz.mapper.AssistCalculateEmployeeMapper;
import com.bage.finance.biz.service.AssistCalculateCateService;
import com.bage.finance.biz.service.AssistCalculateHandleService;
import com.bage.finance.biz.service.SysRegionService;
import com.bage.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

import static com.bage.finance.biz.domain.AssistCalculateEmployeeField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistCalculateEmployeeServiceImpl implements AssistCalculateHandleService {
    final AssistCalculateEmployeeMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;

    @Override
    public AssistCalculateCateCodeEnum getAssistCalculateCateCodeEnum() {
        return AssistCalculateCateCodeEnum.EMPLOYEE;
    }

    /**
     * 添加职员辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean create(Object obj) {
        CreateAssistCalculateEmployeeForm form = (CreateAssistCalculateEmployeeForm) obj;
        AssistCalculateEmployee assistCalculateEmployee = objectConvertor.toAssistCalculateEmployee(form);
        assistCalculateEmployee.initDefault();
        assistCalculateEmployee.setMemberId(tokenService.getThreadLocalUserId());
        assistCalculateEmployee.setUpdateMemberId(tokenService.getThreadLocalUserId());
        assistCalculateEmployee.setTenantId(tokenService.getThreadLocalTenantId());

        return mapper.insert(assistCalculateEmployee) > 0;
    }

    /**
     * 查询客户辅助核算客户列表
     *
     * @return
     */
    @Override
    public List<ListAssistCalculateEmployeeVo> listByAssistCalculateSummaryIds(List<Long> assistCalculateSummaryIds) {
        MyBatisWrapper<AssistCalculateEmployee> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Sex, DepartmentCode, DepartmentName,
                Position, Job, Birthday, StartDate, DepartureDate, Phone, Disable,
                AssistCalculateSummaryId)
                .whereBuilder()
                .andEq(TenantId, tokenService.getThreadLocalTenantId())
                .andEq(DelFlag, false)
                .andIn(AssistCalculateSummaryId, assistCalculateSummaryIds);
        List<AssistCalculateEmployee> assistCalculates = mapper.list(wrapper);
        return objectConvertor.toAssistCalculateEmployee(assistCalculates);
    }

    /**
     * 修改客户辅助核算
     *
     * @param obj
     * @return
     */
    @Override
    public boolean update(Object obj) {
        UpdateAssistCalculateEmployeeForm form = (UpdateAssistCalculateEmployeeForm) obj;
        MyBatisWrapper<AssistCalculateEmployee> wrapper = new MyBatisWrapper<>();
        wrapper.update(UpdateTime, new Date())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(Sex, form.getSex())
                .update(DepartmentCode, form.getDepartmentCode())
                .update(DepartmentName, form.getDepartmentName())
                .update(Position, form.getPosition())
                .update(Job, form.getJob())
                .update(Phone, form.getPhone())
                .update(Birthday, form.getBirthday())
                .update(StartDate, form.getStartDate())
                .update(DepartureDate, form.getDepartureDate())
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
    public GetAssistCalculateEmployeeVo get(long id) {
        AssistCalculateEmployee assistCalculateEmployee = getById(id);
        GetAssistCalculateEmployeeVo result = objectConvertor.toGetAssistCalculateEmployeeVo(assistCalculateEmployee);
        return result;
    }

    /**
     * 查询职员辅助核算明细
     *
     * @param id
     * @return
     */
    private AssistCalculateEmployee getById(long id) {
        MyBatisWrapper<AssistCalculateEmployee> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Sex, DepartmentCode, DepartmentName, Position, Job, Phone, Birthday, StartDate, DepartureDate)
                .whereBuilder()
                .andEq(AssistCalculateSummaryId, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.get(wrapper);
    }
}
