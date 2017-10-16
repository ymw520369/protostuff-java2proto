package org.alan.gen;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.alan.utils.ClassUtils;
import org.alan.utils.FileHelper;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created on 2017/8/31.
 *
 * @author Alan
 * @since 1.0
 */
public class ToOneFile {
    public static void main(String[] args) throws ClassNotFoundException {
        String searchPackage = args[0];
        String genToDir = args[1];
        String clazzName = args[2];
        String pkg = args[3];
        java2PbMessage(searchPackage, pkg, genToDir, Class.forName(clazzName).asSubclass(Annotation.class));
    }

    public static void java2PbMessage(String searchPackage, String pkg, String genToDir, Class<? extends Annotation> protoClazz) {
        String dir = genToDir;
        Set<Class<?>> classes = ClassUtils.getAllClassByAnnotation(searchPackage, protoClazz);
        StringBuilder sb = new StringBuilder();
        String fileName = dir + "/Protocol.proto";
        classes.forEach(clazz -> {
            System.out.println(clazz);
            Schema<?> schema = RuntimeSchema.getSchema(clazz);
            Java2Pb pbGen = new Java2Pb(schema, pkg).gen();
            String content = pbGen.toMesage();
            sb.append(content);
        });
        FileHelper.saveFile(fileName, sb.toString(), false);

    }
}
