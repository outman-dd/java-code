package code.distribution.tcc.utils;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/8
 */
public class TccContainer {

    private static ThreadLocal<String> BRANCH_ID = new ThreadLocal<>();

    public static void setBranchId(String branchId){
        BRANCH_ID.set(branchId);
    }

    public static String getBranchId(){
        return BRANCH_ID.get();
    }

    public static void removeBranchId(){
        BRANCH_ID.remove();
    }

}
