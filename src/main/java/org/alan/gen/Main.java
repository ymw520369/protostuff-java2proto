package org.alan.gen;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.DefaultIdStrategy;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.alan.utils.ClassUtils;
import org.alan.utils.FileHelper;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2017/7/26.
 *
 * @author Alan
 * @since 1.0
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        String searchPackage = args[0];
        String genToDir = args[1];
        String clazzName = args[2];
        String pkg = args[3];
        DefaultIdStrategy idStrategy = (DefaultIdStrategy) RuntimeEnv.ID_STRATEGY;
        idStrategy.registerDelegate(new AtomicLongDelegate());
        java2PbMessage(searchPackage, pkg, genToDir, Class.forName(clazzName).asSubclass(Annotation.class));
    }

    public static void java2PbMessage(String searchPackage, String pkg, String genToDir, Class<? extends Annotation> protoClazz) {
        Map<String, Java2PbMessage> messages = new HashMap<>();
        String dir = genToDir;
        Set<Class<?>> classes = ClassUtils.getAllClassByAnnotation(searchPackage, protoClazz);
        classes.forEach(clazz -> {
            System.out.println(clazz);
            Schema<?> schema = RuntimeSchema.getSchema(clazz);
            Java2PbMessage pbGen = new Java2PbMessage(schema,pkg).gen();
            messages.put(clazz.getSimpleName(), pbGen);
            String fileName = dir + "/" + clazz.getSimpleName() + ".proto";
            System.out.println(fileName);
            String content = pbGen.toMesage();
            FileHelper.saveFile(fileName, content, false);
        });

        messages.values().forEach(pbGen -> pbGen.dependencyMessages.forEach(s -> {
            if (!messages.containsKey(s)) {
                System.err.println("找不到依赖消息,message=" + pbGen.outerClassName + ",dependencyMessage=" + s);
            }
        }));
    }
}
