package com.wnagj.framework.util;

import java.util.List;

/**
 * 通用方法
 */
public class CommonUtils {

    /**
     * 检查List是否可用
     * @param list
     * @return
     */
    public static boolean isEmpty(List list){
        return list != null && list.size() > 0;
    }
}
