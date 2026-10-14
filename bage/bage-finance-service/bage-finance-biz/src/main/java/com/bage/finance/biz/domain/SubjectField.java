package com.bage.finance.biz.domain;

import com.bage.mybatis.help.DbField;
import com.bage.mybatis.help.FieldResult;
import java.util.Collections;

public class SubjectField {
    public static DbField Id = new DbField("id","id","BIGINT","java.lang.Long");

    public static DbField Pid = new DbField("pid","pid","BIGINT","java.lang.Long");

    public static DbField Code = new DbField("code","code","VARCHAR","java.lang.String");

    public static DbField Sort = new DbField("sort","sort","INTEGER","java.lang.Integer");

    public static DbField Name = new DbField("name","name","VARCHAR","java.lang.String");

    public static DbField MnemonicCode = new DbField("mnemonic_code","mnemonicCode","VARCHAR","java.lang.String");

    public static DbField BalanceDirection = new DbField("balance_direction","balanceDirection","TINYINT","java.lang.Integer");

    public static DbField Level = new DbField("level","level","INTEGER","java.lang.Integer");

    public static DbField TenantId = new DbField("tenant_id","tenantId","BIGINT","java.lang.Long");

    public static DbField Disable = new DbField("disable","disable","BIT","java.lang.Boolean");

    public static DbField CreateTime = new DbField("create_time","createTime","TIMESTAMP","java.util.Date");

    public static DbField UpdateTime = new DbField("update_time","updateTime","TIMESTAMP","java.util.Date");

    public static DbField MemberId = new DbField("member_id","memberId","BIGINT","java.lang.Long");

    public static DbField UpdateMemberId = new DbField("update_member_id","updateMemberId","BIGINT","java.lang.Long");

    public static DbField DelFlag = new DbField("del_flag","delFlag","BIT","java.lang.Boolean");

    public static DbField SubjectCate = new DbField("subject_cate","subjectCate","INTEGER","java.lang.Integer");

    public static DbField NodeDepth = new DbField("node_depth","nodeDepth","INTEGER","java.lang.Integer");

    public static DbField DepthCodeLengthOne = new DbField("depth_code_length_one","depthCodeLengthOne","INTEGER","java.lang.Integer");

    public static DbField DepthCodeLengthTwo = new DbField("depth_code_length_two","depthCodeLengthTwo","INTEGER","java.lang.Integer");

    public static DbField DepthCodeLengthThree = new DbField("depth_code_length_three","depthCodeLengthThree","INTEGER","java.lang.Integer");

    public static DbField DepthCodeLengthFour = new DbField("depth_code_length_four","depthCodeLengthFour","INTEGER","java.lang.Integer");

    public static DbField SubjectType = new DbField("subject_type","subjectType","TINYINT","java.lang.Integer");

    public static DbField UseCount = new DbField("use_count","useCount","INTEGER","java.lang.Integer");

    public static DbField ParentSubjectDisable = new DbField("parent_subject_disable","parentSubjectDisable","BIT","java.lang.Boolean");

    public static DbField PidPath = new DbField("pid_path","pidPath","VARCHAR","java.lang.String");

    public static DbField CalculateConfig = new DbField("calculate_config","calculateConfig","LONGVARCHAR","java.lang.String");

    public static FieldResult setId(Long id) {
        return new FieldResult(Id, Collections.singletonList(id));
    }

    public static FieldResult setPid(Long pid) {
        return new FieldResult(Pid, Collections.singletonList(pid));
    }

    public static FieldResult setCode(String code) {
        return new FieldResult(Code, Collections.singletonList(code));
    }

    public static FieldResult setSort(Integer sort) {
        return new FieldResult(Sort, Collections.singletonList(sort));
    }

    public static FieldResult setName(String name) {
        return new FieldResult(Name, Collections.singletonList(name));
    }

    public static FieldResult setMnemonicCode(String mnemonicCode) {
        return new FieldResult(MnemonicCode, Collections.singletonList(mnemonicCode));
    }

    public static FieldResult setBalanceDirection(Integer balanceDirection) {
        return new FieldResult(BalanceDirection, Collections.singletonList(balanceDirection));
    }

    public static FieldResult setLevel(Integer level) {
        return new FieldResult(Level, Collections.singletonList(level));
    }

    public static FieldResult setTenantId(Long tenantId) {
        return new FieldResult(TenantId, Collections.singletonList(tenantId));
    }

    public static FieldResult setDisable(Boolean disable) {
        return new FieldResult(Disable, Collections.singletonList(disable));
    }

    public static FieldResult setCreateTime(java.util.Date createTime) {
        return new FieldResult(CreateTime, Collections.singletonList(createTime));
    }

    public static FieldResult setUpdateTime(java.util.Date updateTime) {
        return new FieldResult(UpdateTime, Collections.singletonList(updateTime));
    }

    public static FieldResult setMemberId(Long memberId) {
        return new FieldResult(MemberId, Collections.singletonList(memberId));
    }

    public static FieldResult setUpdateMemberId(Long updateMemberId) {
        return new FieldResult(UpdateMemberId, Collections.singletonList(updateMemberId));
    }

    public static FieldResult setDelFlag(Boolean delFlag) {
        return new FieldResult(DelFlag, Collections.singletonList(delFlag));
    }

    public static FieldResult setSubjectCate(Integer subjectCate) {
        return new FieldResult(SubjectCate, Collections.singletonList(subjectCate));
    }

    public static FieldResult setNodeDepth(Integer nodeDepth) {
        return new FieldResult(NodeDepth, Collections.singletonList(nodeDepth));
    }

    public static FieldResult setDepthCodeLengthOne(Integer depthCodeLengthOne) {
        return new FieldResult(DepthCodeLengthOne, Collections.singletonList(depthCodeLengthOne));
    }

    public static FieldResult setDepthCodeLengthTwo(Integer depthCodeLengthTwo) {
        return new FieldResult(DepthCodeLengthTwo, Collections.singletonList(depthCodeLengthTwo));
    }

    public static FieldResult setDepthCodeLengthThree(Integer depthCodeLengthThree) {
        return new FieldResult(DepthCodeLengthThree, Collections.singletonList(depthCodeLengthThree));
    }

    public static FieldResult setDepthCodeLengthFour(Integer depthCodeLengthFour) {
        return new FieldResult(DepthCodeLengthFour, Collections.singletonList(depthCodeLengthFour));
    }

    public static FieldResult setSubjectType(Integer subjectType) {
        return new FieldResult(SubjectType, Collections.singletonList(subjectType));
    }

    public static FieldResult setUseCount(Integer useCount) {
        return new FieldResult(UseCount, Collections.singletonList(useCount));
    }

    public static FieldResult setParentSubjectDisable(Boolean parentSubjectDisable) {
        return new FieldResult(ParentSubjectDisable, Collections.singletonList(parentSubjectDisable));
    }

    public static FieldResult setPidPath(String pidPath) {
        return new FieldResult(PidPath, Collections.singletonList(pidPath));
    }

    public static FieldResult setCalculateConfig(String calculateConfig) {
        return new FieldResult(CalculateConfig, Collections.singletonList(calculateConfig));
    }
}