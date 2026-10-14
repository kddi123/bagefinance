package com.bage.finance.biz.service.impl;

import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.domain.MqMsg;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.enums.MqMsgStatusEnum;
import com.bage.finance.biz.mapper.MqMsgMapper;
import com.bage.finance.biz.service.MqMsgService;
import com.bage.mybatis.help.MyBatisWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.bage.finance.biz.domain.MqMsgField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MqMsgServiceImpl implements MqMsgService {
    final MqMsgMapper mapper;
    final TokenService<AdminDTO> tokenService;
    final ObjectMapper objectMapper;
    final ApplicationContext applicationContext;

    /**
     * 保存mq消息
     *
     * @param mqMsg
     * @return
     */
    @Override
    public boolean create(MqMsg mqMsg) {
        mqMsg.initDefault();
        mqMsg.setMemberId(tokenService.getThreadLocalUserId());
        mqMsg.setUpdateMemberId(tokenService.getThreadLocalUserId());
        mqMsg.setTenantId(tokenService.getThreadLocalTenantId());
        if (Strings.isBlank(mqMsg.getRequestId())) {
            mqMsg.setRequestId(UUID.randomUUID().toString());
        }
        return mapper.insert(mqMsg) > 0;
    }

    /**
     * 消费成功
     *
     * @param mqMsg
     * @return
     */
    @Override
    public boolean success(MqMsg mqMsg) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.update(MsgStatus, MqMsgStatusEnum.SUCCESS.getCode())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder().andEq(Id, mqMsg.getId())
                .andEq(MsgStatus, MqMsgStatusEnum.WAIT.getCode());
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 消费失败
     *
     * @param mqMsg
     * @return
     */
    @Override
    public boolean fail(MqMsg mqMsg) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(ErrorCount, 1)
                .update(ErrorMsg, mqMsg.getErrorMsg())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder().andEq(Id, mqMsg.getId())
                .andEq(MsgStatus, MqMsgStatusEnum.WAIT.getCode());
        if (mqMsg.getMsgStatus().equals(MqMsgStatusEnum.FAIL.getCode())) {
            wrapper.update(MsgStatus, MqMsgStatusEnum.FAIL.getCode());
        }
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 获取一条mq消息
     *
     * @param id
     * @return
     */
    @Override
    public MqMsg get(long id) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, MsgStatus, RequestId)
                .whereBuilder()
                .andEq(Id, id);
        return mapper.get(wrapper);
    }

    /**
     * 获取一条mq消息
     *
     * @param requestId
     * @return
     */
    @Override
    public MqMsg get(String requestId) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, RequestId, ErrorCount, MsgStatus, MqTopic, MqTag, MqKey, DataNo)
                .whereBuilder()
                .andEq(RequestId, requestId);
        return mapper.get(wrapper);
    }

    /**
     * 通过定时任务处理消息
     */
    @Override
    public void handleMqMessage() {
        long minId = 0;
        List<MqMsg> mqMsgs = null;
        while (!CollectionUtils.isEmpty(mqMsgs = listWaitMsg(minId))) {
            for (MqMsg mqMsg : mqMsgs) {
                try {
                    log.info("正在处理消息：{}", mqMsg.getRequestId());
                    Object obj = objectMapper.readValue(mqMsg.getMsg(), Class.forName(mqMsg.getMsgClassName()));
                    applicationContext.publishEvent(obj);
                } catch (Exception ex) {
                    log.error("消费消息异常", ex);
                } finally {
                    minId = mqMsg.getId();
                }
            }
        }
    }

    /**
     * 消费失败
     *
     * @param mqMsg
     * @return
     */
    @Override
    public boolean failByRequestId(MqMsg mqMsg) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.updateIncr(ErrorCount, 1)
                .update(ErrorMsg, mqMsg.getErrorMsg())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder().andEq(RequestId, mqMsg.getRequestId())
                .andEq(MsgStatus, MqMsgStatusEnum.WAIT.getCode());
        if (mqMsg.getMsgStatus().equals(MqMsgStatusEnum.FAIL.getCode())) {
            wrapper.update(MsgStatus, MqMsgStatusEnum.FAIL.getCode());
        }
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 消费成功
     *
     * @param requestId
     * @return
     */
    @Override
    public boolean success(String requestId) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.update(MsgStatus, MqMsgStatusEnum.SUCCESS.getCode())
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder().andEq(RequestId, requestId)
                .andEq(MsgStatus, MqMsgStatusEnum.WAIT.getCode());
        return mapper.updateField(wrapper) > 0;
    }

    /**
     * 获取待处理的消息
     *
     * @param minId
     * @return
     */
    private List<MqMsg> listWaitMsg(long minId) {
        MyBatisWrapper<MqMsg> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, RequestId, MsgClassName, Msg, ErrorCount, DataNo)
                .whereBuilder()
                //id大于当前id
                .andGT(Id, minId)
                //错误次数必须是小于或等于6
                .andLTE(ErrorCount, 6)
                //状态是待处理的消息
                .andEq(MsgStatus, MqMsgStatusEnum.WAIT.getCode())
                //更新时间必须是超过1分钟
                .andLTE(UpdateTime, DateUtil.toDate(LocalDateTime.now().plusMinutes(-1)));
        wrapper.limit(100);
        wrapper.orderByAsc(Id);
        return mapper.list(wrapper);
    }
}
