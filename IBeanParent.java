package com.hw.dp.dsp.dedicated.shanxi.mask.utils.parent;

/**
 * @author LYL-PC
 * @所属项目 DP_DSP_Dedicated_ShanXi_Mask
 * @类名称 IBeanParent
 * @类作用
 * @类作者 LYL-PC
 * @创建日期 2020/1/20
 * @审核人
 * @审核日期
 * @更新记录
 * @其它备注
 */
public interface IBeanParent {
    /**
     * 统一get方法
     *
     * @param name
     * @param <T>
     * @return
     */
    <T> T get(String name);
}
