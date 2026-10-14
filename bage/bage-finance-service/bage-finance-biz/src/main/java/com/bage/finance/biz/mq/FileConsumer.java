package com.bage.finance.biz.mq;

import com.bage.common.exception.BizException;
import com.bage.finance.biz.constant.MqConstant;
import com.bage.finance.biz.domain.MqMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 消息消费方
 * 1.如果两个消费者group和topic都一样，则二者轮循接收消息
 * 2.如果两个消费者topic一样，而group不一样，则消息变成广播机制
 * RocketMQListener<>泛型必须和接收的消息类型相同
 */
@Service
@RocketMQMessageListener(
        //topic：消息的发送者使用同一个topic
        topic = MqConstant.TOPIC_FILE,
        //group：不用和生产者group相同 ( 在RocketMQ中消费者和发送者组没有关系 )
        consumerGroup = "consumer-group-2",
        //tag：设置为 * 时，表示全部。多个以|分隔，比如：tag1|tag2。
        selectorExpression = "*",
        //指定需要消费的TAG
        selectorType = SelectorType.TAG,
        //消费模式：默认 CLUSTERING （ CLUSTERING：负载均衡 ）（ BROADCASTING：广播机制 ）
        messageModel = MessageModel.CLUSTERING
)
@RequiredArgsConstructor
@Slf4j
public class FileConsumer implements RocketMQListener<MqMsg> {
    final ApplicationContext applicationContext;
    final ObjectMapper objectMapper;

    @Override
    public void onMessage(MqMsg mqMsg) {
        try {
            Object obj = objectMapper.readValue(mqMsg.getMsg(), Class.forName(mqMsg.getMsgClassName()));
            applicationContext.publishEvent(obj);
        } catch (Exception ex) {
            throw new BizException("消费异常", ex);
        }
    }
}
