package com.dongxun.lichunkai.cloudmusic.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据省份编码查询城市
 */
public class ProvinceAndCodeUtil {
    private static Map<String,String>  provinceKeyMap = new HashMap();
    private static Map<String,String> codeKeyMap = new HashMap();

    static{
        //直辖市
//        provinceKeyMap.put("北京市","11");
//        codeKeyMap.put("11","北京市");
//        provinceKeyMap.put("天津市","12");
//        codeKeyMap.put("12","天津市");
//        provinceKeyMap.put("上海市","31");
//        codeKeyMap.put("31","上海市");
//        provinceKeyMap.put("重庆市","50");
//        codeKeyMap.put("50","重庆市");
        provinceKeyMap.put("直辖市","11");
        codeKeyMap.put("11","直辖市");
        provinceKeyMap.put("直辖市","12");
        codeKeyMap.put("12","直辖市");
        provinceKeyMap.put("直辖市","31");
        codeKeyMap.put("31","直辖市");
        provinceKeyMap.put("直辖市","50");
        codeKeyMap.put("50","直辖市");
        //特别行政区
//        provinceKeyMap.put("香港特别行政区","81");
//        codeKeyMap.put("81","香港特别行政区");
//        provinceKeyMap.put("澳门特别行政区","82");
//        codeKeyMap.put("82","澳门特别行政区");
        provinceKeyMap.put("特别行政区","81");
        codeKeyMap.put("81","特别行政区");
        provinceKeyMap.put("特别行政区","82");
        codeKeyMap.put("82","特别行政区");
        //省份
        provinceKeyMap.put("河北省","13");
        codeKeyMap.put("13","河北省");
        provinceKeyMap.put("山西省","14");
        codeKeyMap.put("14","山西省");
        provinceKeyMap.put("内蒙古自治区","15");
        codeKeyMap.put("15","内蒙古自治区");
        provinceKeyMap.put("辽宁省","21");
        codeKeyMap.put("21","辽宁省");
        provinceKeyMap.put("吉林省","22");
        codeKeyMap.put("22","吉林省");
        provinceKeyMap.put("黑龙江省","23");
        codeKeyMap.put("23","黑龙江省");
        provinceKeyMap.put("江苏省","32");
        codeKeyMap.put("32","江苏省");
        provinceKeyMap.put("浙江省","33");
        codeKeyMap.put("33","浙江省");
        provinceKeyMap.put("安徽省","34");
        codeKeyMap.put("34","安徽省");
        provinceKeyMap.put("福建省","35");
        codeKeyMap.put("35","福建省");
        provinceKeyMap.put("江西省","36");
        codeKeyMap.put("36","江西省");
        provinceKeyMap.put("山东省","37");
        codeKeyMap.put("37","山东省");
        provinceKeyMap.put("河南省","41");
        codeKeyMap.put("41","河南省");
        provinceKeyMap.put("湖北省","42");
        codeKeyMap.put("42","湖北省");
        provinceKeyMap.put("湖南省","43");
        codeKeyMap.put("43","湖南省");
        provinceKeyMap.put("广东省","44");
        codeKeyMap.put("44","广东省");
        provinceKeyMap.put("广西壮族自治区","45");
        codeKeyMap.put("45","广西壮族自治区");
        provinceKeyMap.put("海南省","46");
        codeKeyMap.put("46","海南省");
        provinceKeyMap.put("四川省","51");
        codeKeyMap.put("51","四川省");
        provinceKeyMap.put("贵州省","52");
        codeKeyMap.put("52","贵州省");
        provinceKeyMap.put("云南省","53");
        codeKeyMap.put("53","云南省");
        provinceKeyMap.put("西藏自治区","54");
        codeKeyMap.put("54","西藏自治区");
        provinceKeyMap.put("陕西省","61");
        codeKeyMap.put("61","陕西省");
        provinceKeyMap.put("甘肃省","62");
        codeKeyMap.put("62","甘肃省");
        provinceKeyMap.put("青海省","63");
        codeKeyMap.put("63","青海省");
        provinceKeyMap.put("宁夏回族自治区","64");
        codeKeyMap.put("64","宁夏回族自治区");
        provinceKeyMap.put("新疆维吾尔自治区","65");
        codeKeyMap.put("65","新疆维吾尔自治区");
        provinceKeyMap.put("台湾省","71");
        codeKeyMap.put("71","台湾省");
    }

    public static String getCodeByCity(String city){
        return provinceKeyMap.get(city);
    }
    public static String getCityByCode(String code){
        return codeKeyMap.get(code);
    }
}
