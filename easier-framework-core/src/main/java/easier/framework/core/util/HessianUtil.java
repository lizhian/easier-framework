package easier.framework.core.util;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessianUtil {

    @SneakyThrows
    public static byte[] serialize(Object obj) {
        if (obj == null) {
            return new byte[0];
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(os);
        hessian2Output.writeObject(obj);
        hessian2Output.close();
        return os.toByteArray();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(is);
        return (T) hessian2Input.readObject();
    }

}
