package com.bage.finance.biz.config;

import com.bage.finance.biz.domain.*;
import com.bage.finance.biz.domain.es.VoucherDocuemt;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.*;
import com.bage.mybatis.help.PageInfo;
import com.bage.wx.dto.MpQrCodeCreateResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectConvertor {
    GenerateMpRegCodeVo toGenerateMpRegCodeResponse(MpQrCodeCreateResult source);

    @Mappings({@Mapping(target = "avatar", source = "avatarUrl")})
    CurrentInfoVo toCurrentInfoVo(Member source);

    @Mappings({@Mapping(target = "key", source = "id"),
            @Mapping(target = "title", source = "name")})
    ListTreeMenuVo toListTreeMenuVo(SysMenu source);

    List<ListTreeMenuVo> toListTreeMenuVo(List<SysMenu> source);

    @Mappings({@Mapping(target = "value", source = "id"),
            @Mapping(target = "title", source = "name")})
    ListTreeSelectMenuVo toListTreeSelectMenuVo(SysMenu source);

    List<ListTreeSelectMenuVo> toListTreeSelectMenuVo(List<SysMenu> source);

    GetMenuByIdVo toGetMenuByIdVo(SysMenu source);

    SysMenu toSysMenu(CreateMenuForm source);

    SysResource toSysResource(CreateSysResourceForm source);
    GetSysResourceVo toGetSysResourceVo(SysResource source);
    List<ListSysResourceVo> toListSysResourceVo(List<SysResource> source);

    PageInfo<ListRoleVo> toListRoleVoPage(PageInfo<SysRole> source);
    GetRoleDetailVo toGetRoleDetailVo(SysRole source);

    MenuDataItemVo toMenuDataItemVo(SysMenu source);

    List<MenuDataItemVo> toMenuDataItemVo(List<SysMenu> source);

    GetAccountBookVo toGetAccountBookVo(AccountBook source);

    @Mappings({
            @Mapping(target = "valueAddedTaxCate", expression = "java(com.bage.finance.biz.enums.ValueAddedTaxCateEnum.getMessage(source.getValueAddedTaxCate()))"),
            @Mapping(target = "accountingStandard", expression = "java(com.bage.finance.biz.enums.AccountingStandardEnum.getMessage(source.getAccountingStandard()))")
    })
    ListAccountBookVo toListAccountBookVo(AccountBook source);

    PageInfo<ListAccountBookVo> toListAccountBookVoPage(PageInfo<AccountBook> source);
    AccountBook toAccountBook(AddAccountBookForm source);

    List<DataDictionaryVo> toDataDictionaryVo(List<DataDictionary> source);

    List<ListCurrencyConfigVo> toListCurrencyConfigVo(List<CurrencyConfig> source);

    CurrencyConfig toCurrencyConfig(CreateCurrencyConfigForm source);

    GetCurrencyConfigVo toGetCurrencyConfigVo(CurrencyConfig source);

    VoucherWordConfig toVoucherWordConfig(CreateVoucherWordConfigForm source);

    GetVoucherWordConfigVo toGetVoucherWordConfigVo(VoucherWordConfig source);

    List<ListVoucherWordConfigVo> toListVoucherWordConfigVo(List<VoucherWordConfig> source);

    List<ListCalculateCateVo> toListCalculateCateVos(List<AssistCalculateCate> source);

    AssistCalculateCate toAssistCalculateCate(CreateAssistCalculateCateForm source);

    GetAssistCalculateCateVo toGetAssistCalculateCateVo(AssistCalculateCate source);

    PageInfo<ListAssistCalculateSummaryVo> toListAssistCalculateSummaryVo(PageInfo<AssistCalculateSummary> source);
    AssistCalculateSummary toAssistCalculateSummary(CreateAssistCalculateForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateCustomerForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateCustomForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateDepartmentForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateEmployeeForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateInventoryForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateProjectForm source);

    AssistCalculateSummary toAssistCalculateSummary(UpdateAssistCalculateSupplierForm source);

    AssistCalculateCashFlow toAssistCalculateCashFlow(CreateAssistCalculateCashFlowForm source);
    List<ListAssistCalculateCashFlowVo> toListAssistCalculateCashFlowVo(List<AssistCalculateCashFlow> source);
    GetAssistCalculateCashFlowVo toGetAssistCalculateCashFlowVo(AssistCalculateCashFlow source);

    AssistCalculateCustomer toAssistCalculateCustomer(CreateAssistCalculateCustomerForm source);
    List<ListAssistCalculateCustomerVo> toListAssistCalculateCustomerVo(List<AssistCalculateCustomer> source);
    GetAssistCalculateCustomerVo toGetAssistCalculateCustomerVo(AssistCalculateCustomer source);

    AssistCalculateCustom toAssistCalculateCustom(CreateAssistCalculateCustomForm source);
    List<ListAssistCalculateCustomVo> toListAssistCalculateVo(List<AssistCalculateCustom> source);
    GetAssistCalculateCustomVo toGetAssistCalculateCustomVo(AssistCalculateCustom source);

    AssistCalculateDepartment toAssistCalculateDepartment(CreateAssistCalculateDepartmentForm source);
    List<ListAssistCalculateDepartmentVo> toAssistCalculateDepartmentVo(List<AssistCalculateDepartment> source);
    GetAssistCalculateDepartmentVo toGetAssistCalculateDepartmentVo(AssistCalculateDepartment source);

    AssistCalculateEmployee toAssistCalculateEmployee(CreateAssistCalculateEmployeeForm source);
    List<ListAssistCalculateEmployeeVo> toAssistCalculateEmployee(List<AssistCalculateEmployee> source);
    GetAssistCalculateEmployeeVo toGetAssistCalculateEmployeeVo(AssistCalculateEmployee source);

    AssistCalculateInventory toAssistCalculateInventory(CreateAssistCalculateInventoryForm source);
    List<ListAssistCalculateInventoryVo> toListAssistCalculateInventoryVo(List<AssistCalculateInventory> source);
    GetAssistCalculateInventoryVo toGetAssistCalculateInventoryVo(AssistCalculateInventory source);

    AssistCalculateProject toAssistCalculateProject(CreateAssistCalculateProjectForm source);
    List<ListAssistCalculateProjectVo> toListAssistCalculateProjectVo(List<AssistCalculateProject> source);
    GetAssistCalculateProjectVo toGetAssistCalculateProjectVo(AssistCalculateProject source);

    AssistCalculateSupplier toAssistCalculateSupplier(CreateAssistCalculateSupplierForm source);
    List<ListAssistCalculateSupplierVo> toListAssistCalculateSupplierVo(List<AssistCalculateSupplier> source);
    GetAssistCalculateSupplierVo toGetAssistCalculateSupplierVo(AssistCalculateSupplier source);

    Subject toSubject(CreateSubjectForm source);
    GetSubjectVo toGetSubjectVo(Subject source);
    SubjectCalculateConfigForm.NumberCalculateConfig toNumberCalculateConfig(SubjectCalculateConfigVo.NumberCalculateConfig source);
    List<SubjectCalculateConfigForm.AssistCalculateConfig> toAssistCalculateConfig(List<SubjectCalculateConfigVo.AssistCalculateConfig> source);
    ListSubjectVo toListSubjectVo(Subject source);
    GetSubjectDetailVo toGetSubjectDetailVo(Subject source);
    List<GetSubjectDetailVo.ForeignCurrencyConfigVo> toForeignCurrencyConfigVo(List<CurrencyConfig> source);
    List<ListSubjectByCateAndCodeAndNameVo> toListSubjectByCateAndCodeAndNameVo(List<Subject> source);
    List<GetSubjectDetailVo.AssistCalculateConfigVo> toAssistCalculateConfigVo(List<AssistCalculateCate> source);
    DownloadSubjectVo toDownloadSubjectVo(Subject source);

    Voucher toVoucher(CreateVoucherForm source);
    List<VoucherSubjectDetail> toVoucherSubjectDetail(List<CreateVoucherForm.VoucherSubjectDetailForm> source);
    List<VoucherSubjectAssistCalculateDetail> toVoucherSubjectAssistCalculateDetail(List<CreateVoucherForm.VoucherSubjectAssistCalculateDetailForm> source);
    @Mappings({@Mapping(target = "voucherSubjectDetailVoList", source = "subjectDocuments")})
    ListVoucherVo toListVoucherVo(VoucherDocuemt source);
    PageInfo<ListVoucherVo> toListVoucherVo(PageInfo<VoucherDocuemt> source);
    GetVoucherVo toGetVoucherVo(Voucher source);
    List<GetVoucherVo.VoucherSubjectDetailVo> toGetVoucherSubjectDetailVo(List<VoucherSubjectDetail> source);
    List<GetVoucherVo.ForeignCurrencyConfigVo> toForeignCurrencyConfigVo2(List<CurrencyConfig> source);
    VoucherDocuemt toVoucherES(Voucher source);
    List<VoucherDocuemt.SubjectDocument> toSubjectES(List<VoucherSubjectDetail> source);
    List<VoucherDocuemt.AssistCalculateDocument> toAssistCalculateES(List<VoucherSubjectAssistCalculateDetail> source);

    List<ListMemberVo> toListMemberVo(List<Member> source);

    Folder toFolder(CreateFolderForm source);

    GetFolderVo toGetFolderVo(Folder source);

    List<ListFolderVo> toListFolderVo(List<Folder> source);

    ListFileVo toListFileVo(File source);
}