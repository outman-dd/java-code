package code.algorithm.hash;

import code.collection.hashtable.HashMap;
import code.util.Encrypt;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * 〈密码加密〉<p>
 *  sha256(md5(password) + slat)
 *
 * @author zixiao
 * @date 2019/12/10
 */
public class PasswordEncrypt {

    private static HashMap<String, UserLogin> userLoginTable = new HashMap<>();

    @Data
    @AllArgsConstructor
    class UserLogin implements Serializable {

        String password;

        String slat;

    }

    public boolean register(String userName, String password){
        if(userLoginTable.contains(userName)){
            return false;
        }
        userLoginTable.put(userName, register(password));
        return true;
    }

    private UserLogin register(String password){
        String slat = UUID.randomUUID().toString().substring(0, 8);
        String pwd = Encrypt.encrypt(password, slat);
        System.out.println("加密password："+pwd + "，size:"+pwd.length());
        return new UserLogin(pwd, slat);
    }

    public boolean login(String userName, String password){
        UserLogin login = userLoginTable.get(userName);
        if(login == null){
            System.out.println("用户不存在");
            return false;
        }
        String pwd = Encrypt.encrypt(password, login.getSlat());
        if(pwd.equals(login.getPassword())){
            System.out.println("用户["+userName+"]登录成功");
            return true;
        }
        System.out.println("密码错误");
        return false;
    }

    public static void main(String[] args) {
        PasswordEncrypt encrypt = new PasswordEncrypt();
        encrypt.register("beston", "123456");
        encrypt.register("a", "z3lll39399440030sk2828000300e30d200309a9393er99392f9499lels");
        encrypt.register("b", "AAA"+System.currentTimeMillis());
        encrypt.register("c", UUID.randomUUID().toString());
        encrypt.login("beston", "123456");
    }
}
