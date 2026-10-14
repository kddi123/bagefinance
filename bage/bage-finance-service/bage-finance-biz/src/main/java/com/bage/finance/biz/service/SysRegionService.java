package com.bage.finance.biz.service;

public interface SysRegionService {
    /**
     * 获取区县信息
     *
     * @param provinceCode
     * @param cityCode
     * @param countyCode
     * @return
     */
    void checkRegionCode(String provinceCode, String cityCode, String countyCode);
}
