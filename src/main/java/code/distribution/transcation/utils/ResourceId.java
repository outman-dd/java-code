package code.distribution.transcation.utils;

import org.springframework.util.Assert;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/26
 */
public class ResourceId {

    private static final String SEP = "@";

    public static String newResourceId(String bizType){
        return bizType + SEP + Thread.currentThread().getName();
    }

    public static String getBizType(String resourceId){
        Assert.isTrue(resourceId.contains(SEP), "");
        return resourceId.split(SEP)[0];
    }

}
