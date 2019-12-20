package code.biz.error;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/28
 */
public enum ErrorCode {

    /**
     * 参数错误，不可重试
     * 入参及通过入参获取的数据不符合要求
     */
    ARGMENT_ERROR,

    /**
     * 平台错误，不可重试
     * 接口方法不存在，权限不够，配置缺少等情况
     */
    PLATFORM_ERROR,

    /**
     * 系统错误，可重试
     * RPC异常，接口超时，服务不可用等
     */
    SYSTEM_ERROR

}
