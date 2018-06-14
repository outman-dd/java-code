package code.performance.cat;

import java.io.Serializable;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/12
 */
public class Result<T> implements Serializable{

    private boolean isSuccess;

    /**
     * 结果
     */
    private List<T> resultList;

    private String errorCode;

    private String errorMsg;

    public Result(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public static <T> Result buildSuccess(List<T> resultList){
        Result ret = new Result(true);
        ret.setResultList(resultList);
        return ret;
    }

    public static Result buildFail(String errorCode, String errorMsg){
        Result ret = new Result(false);
        ret.setErrorCode(errorCode);
        ret.setErrorMsg(errorMsg);
        return ret;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}