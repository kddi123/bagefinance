package com.bage.wx;

import com.bage.wx.dto.AccessTokenResult;
import com.bage.wx.dto.MpQrCodeCreateRequest;
import com.bage.wx.dto.MpQrCodeCreateResult;
import com.bage.wx.service.WXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@SpringBootTest
public class WXTest {
    @Autowired
    private WXService wxService;

    @Test
    public void TestGetMpAccessToken() {
        AccessTokenResult accessTokenResult = getMpAccessToken();
        System.out.println(accessTokenResult.toString());
    }

    @Test
    public void TestCreateMpQrcodeCreate() {
        AccessTokenResult accessTokenResult = getMpAccessToken();
        MpQrCodeCreateRequest request = new MpQrCodeCreateRequest();
        request.setExpireSeconds(600);
        request.setActionName("QR_STR_SCENE");
        request.setActionInfo(request.new ActionInfo());
        request.getActionInfo().setScene(request.new scene());
        request.getActionInfo().getScene().setSceneStr("ScanReg_" + UUID.randomUUID().toString());
        MpQrCodeCreateResult result = wxService.createMpQrcodeCreate(accessTokenResult.getAccessToken(), request);
        System.out.println(result.toString());
    }

    private AccessTokenResult getMpAccessToken() {
        // 副业账号
        //AccessTokenResult accessTokenResult = wxService.getMpAccessToken("wxeb937d26b259854b", "8e96c570e234fa727f70b2149a2c1919");
        //微信测试号-token：504e3c5d01fd4e4b828acfe47c7137d9
        AccessTokenResult accessTokenResult = wxService.getMpAccessToken("wx704a38aacbc1e64d", "3725a9046403754a25b44332633dd22b");

        return accessTokenResult;
    }
}
