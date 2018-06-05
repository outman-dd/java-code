package code.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 〈Hessian〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/5
 */
public class HessianSerializer implements ISerializer {

    @Override
    public <T> byte[] serialize(T obj){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        try {
            ho.writeObject(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return os.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(is);
        try {
            return (T)hi.readObject(clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
