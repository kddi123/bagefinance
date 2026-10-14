package com.bage.finance.admin.api.controller;

import com.bage.wx.aes.AesException;
import com.bage.wx.dto.MpCommonRequest;
import com.bage.wx.service.WxMpEventService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * 微信模块
 */
@RestController
@RequestMapping(value = "/wxEvent")
@RequiredArgsConstructor
@Slf4j
public class WxEventController {
    final WxMpEventService wxMpEventService;

    /**
     * 接收微信事件推送
     * 参考文档：
     * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html
     * *
     *
     * @param mpCommonRequest
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @RequestMapping(value = "/receiveMpEvent",
            method = {RequestMethod.GET, RequestMethod.POST})
    public String receiveMpEvent(@Validated @ModelAttribute MpCommonRequest mpCommonRequest, HttpServletRequest httpServletRequest) throws IOException, AesException {
        return wxMpEventService.receiveMpEvent(mpCommonRequest, httpServletRequest);
    }
}
