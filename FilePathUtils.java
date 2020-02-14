package com.hw.dp.dsp.dedicated.shanxi.mask.utils;

import com.hw.hwcdp.publiccode.global.SystemInit;

import java.net.URI;
import java.net.URL;

/**
 * @author LYL-PC
 * @所属项目 DP_DSP_Dedicated_ShanXi_FarmForecast
 * @类名称 FilePathUtils
 * @类作用 文件路径工具类
 * @类作者 LYL-PC
 * @创建日期 2020/1/13
 * @审核人
 * @审核日期
 * @更新记录
 * @其它备注
 */
public class FilePathUtils {
    public static final String PROTOCOL_JAR = "jar";
    /**
     * 获取文件路径
     *      ide本地开发：classes路径
     *      打成jar后：jar包同级路径
     * @param fileName 文件名
     * @return 最终路径+文件名
     */
    public static String getFullPath(String fileName) {
        String path = "";
        try {
            URL resource = FilePathUtils.class.getResource("");
            String protocol = resource.getProtocol();
            if(PROTOCOL_JAR.equals(protocol)) {
                //jar 包
                path = SystemInit.jarCurDir + fileName;
            }else {
                //ide 工具本地测试
                URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
                URI uri = url.toURI();
                path = uri.getPath();
            }
        }catch (Exception e) {
            //优先保证正式环境，所以不打印异常日志，正式环境一定是jar包同级目录
            path = SystemInit.jarCurDir + fileName;
        }
        return path;
    }
}
