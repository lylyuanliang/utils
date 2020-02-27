package com.hw.hwcdp3.dp.gridsystem.gfe.deletedata.utils;

import java.util.UUID;

/**
 * 所属项目：DP3_GridSystem_GFE_DeleteData
 * 类名称：UUIDUtils
 * 类作用：uuid工具类
 * 类作者：LYL-PC
 * 创建日期：2019/12/2
 * 审核人：
 * 审核日期：
 * 更新记录：
 * 其它备注：
 */
public class UUIDUtils {
    /**
     * 带-的UUID
     *
     * @return 36位的字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 去掉-的UUID
     *
     * @return 32位的字符串
     */
    public static String getUUIDWithoutSeparator() {
        return getUUID().replaceAll("-", "");
    }
    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID(int number){
        if(number < 1){
            return null;
        }
        String[] ss = new String[number];
        for(int i=0;i<number;i++){
            ss[i] = getUUID();
        }
        return ss;
    }
}
