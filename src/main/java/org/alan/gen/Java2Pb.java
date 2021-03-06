package org.alan.gen;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.WireFormat;
import com.dyuproject.protostuff.runtime.EnumIO;
import com.dyuproject.protostuff.runtime.HasSchema;
import com.dyuproject.protostuff.runtime.MappedSchema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import net.webby.protostuff.runtime.Pair;
import net.webby.protostuff.runtime.ReflectionUtil;
import net.webby.protostuff.runtime.RuntimeFieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created on 2017/6/14.
 *
 * @author Alan
 * @since 1.0
 */
public class Java2Pb {
    /* 协议*/
    public final Schema<?> schema;
    /* 本类message*/
    public StringBuilder message = new StringBuilder();

    public Java2Pb(Schema<?> schema, String pkg) {
        if (!(schema instanceof RuntimeSchema)) {
            throw new IllegalArgumentException("schema instance must be a RuntimeSchema");
        }

        this.schema = schema;
    }

    public Java2Pb gen() {
        generateInternal();
        return this;
    }

    public String toMesage() {
        return message.toString();
    }

    protected void generateInternal() {
        if (schema.typeClass().isEnum()) {
            doGenerateEnum(schema.typeClass());
        } else {
            doGenerateMessage(schema);
        }
    }

    protected void doGenerateEnum(Class<?> enumClass) {

        message.append("enum ").append(enumClass.getSimpleName()).append(" {").append("\n");

        for (Object val : enumClass.getEnumConstants()) {
            Enum<?> v = (Enum<?>) val;
            message.append("  ").append(val).append(" = ").append(v.ordinal()).append(";\n");
        }

        message.append("}").append("\n\n");

    }

    protected void doGenerateMessage(Schema<?> schema) {

        if (!(schema instanceof RuntimeSchema)) {
            throw new IllegalStateException("invalid schema type " + schema.getClass());
        }

        RuntimeSchema<?> runtimeSchema = (RuntimeSchema<?>) schema;

        message.append("message ").append(runtimeSchema.messageName()).append(" {").append("\n");

        try {
            Field fieldsField = MappedSchema.class.getDeclaredField("fields");
            fieldsField.setAccessible(true);
            com.dyuproject.protostuff.runtime.MappedSchema.Field<?>[] fields = (com.dyuproject.protostuff.runtime.MappedSchema.Field<?>[]) fieldsField
                    .get(runtimeSchema);

            for (int i = 0; i != fields.length; ++i) {

                com.dyuproject.protostuff.runtime.MappedSchema.Field<?> field = fields[i];
                String fieldType = null;
                if (field.type == WireFormat.FieldType.ENUM) {

                    Field reflectionField = field.getClass().getDeclaredField("val$eio");
                    reflectionField.setAccessible(true);
                    EnumIO enumIO = (EnumIO) reflectionField.get(field);
                    fieldType = enumIO.enumClass.getSimpleName();
                } else if (field.type == WireFormat.FieldType.MESSAGE) {
                    if (field.repeated) {
                        Field typeClassField = field.getClass().getField("typeClass");
                        typeClassField.setAccessible(true);
                        Class<?> tmpClass = (Class<?>) typeClassField.get(field);
                        fieldType = tmpClass.getSimpleName();
                    } else {
                        Pair<RuntimeFieldType, Class<?>> normField = ReflectionUtil.normalizeFieldClass(field);
                        if (normField == null) {
                            throw new IllegalStateException(
                                    "unknown fieldClass " + field.getClass());
                        }

                        Class<?> fieldClass = normField.getSecond();
                        if (normField.getFirst() == RuntimeFieldType.RuntimeRepeatedField) {

                        } else if (normField.getFirst() == RuntimeFieldType.RuntimeMessageField) {

                            Field typeClassField = fieldClass.getDeclaredField("typeClass");
                            typeClassField.setAccessible(true);
                            Class<?> typeClass = (Class<?>) typeClassField.get(field);

                            Field hasSchemaField = fieldClass.getDeclaredField("hasSchema");
                            hasSchemaField.setAccessible(true);

                            HasSchema<?> hasSchema = (HasSchema<?>) hasSchemaField.get(field);
                            Schema<?> fieldSchema = hasSchema.getSchema();
                            fieldType = fieldSchema.messageName();

//                        } else if (normField.getFirst() == RuntimeFieldType.RuntimeMapField ||
//                                normField.getFirst() == RuntimeFieldType.RuntimeObjectField) {
//                            Field schemaField = fieldClass.getDeclaredField("schema");
//                            schemaField.setAccessible(true);
//                            Schema<?> fieldSchema = (Schema<?>) schemaField.get(field);
//
//                            if ("Array".equals(fieldSchema.messageName())){
//                                Field hsField= fieldSchema.getClass().getDeclaredField("hs");
//                                hsField.setAccessible(true);
//                                HasSchema hasSchema = (HasSchema) hsField.get(fieldSchema);
//                                Field typeClassField= hasSchema.getClass().getDeclaredField("typeClass");
//                                typeClassField.setAccessible(true);
//                                Class clazz = (Class) typeClassField.get(hasSchema);
//                                System.out.println("");
//                            }
//
//                            Pair<RuntimeSchemaType, Class<?>> normSchema = ReflectionUtil.normalizeSchemaClass(fieldSchema.getClass());
//                            System.out.println("1、"+fieldSchema.messageName());
//                            System.out.println("2、"+fieldSchema.typeClass());
//                            System.out.println("3、"+fieldSchema.getClass().getName());
//                            System.out.println("4、"+fieldSchema.messageFullName());

//
//                            if (normSchema == null) {
//                                throw new IllegalStateException("unknown schema type " + fieldSchema.getClass());
//                            }
//
//                            switch (normSchema.getFirst()) {
//                                case ArraySchema:
//                                    fieldType = "ArrayObject";
//                                    break;
//                                case ObjectSchema:
//                                    fieldType = "DynamicObject";
//                                    break;
//                                case MapSchema:
//
//                                    Field reflectionField = field.getClass().getDeclaredField("val$f");
//                                    reflectionField.setAccessible(true);
//                                    Field pojoField = (Field) reflectionField.get(field);
//
//                                    Pair<Type, Type> keyValue = ReflectionUtil.getMapGenericTypes(pojoField.getGenericType());
//
//                                    fieldType = getMapFieldType(keyValue);
//                                    break;
//
//                                case PolymorphicEnumSchema:
//                                    fieldType = "EnumObject";
//                                    break;
//                            }

                            //System.out.println(getClassHierarchy(normSchema.getSecond()));

                        } else {
                            throw new IllegalStateException("field type not support, typeclass=" + schema.typeClass() + ",fieldName=" + field.name);
                        }
                    }
                } else {
                    fieldType = field.type.toString().toLowerCase();
                }

                message.append("  ");

                if (field.type == WireFormat.FieldType.ENUM) {
                    message.append("required ");
                } else {
                    if (field.repeated) {
                        message.append("repeated ");
                    } else {
                        message.append("optional ");
                    }
                }
                message.append(fieldType).append(" ").append(field.name).append(" = ").append(field.number).append(";\n");

            }

        } catch (Exception e) {
            throw new RuntimeException("generate proto fail", e);
        }

        message.append("}").append("\n\n");

    }

    private static String getMapFieldType(Pair<Type, Type> keyValue) {
        if (keyValue.getFirst() == String.class) {
            if (keyValue.getSecond() == String.class) {
                return "MapStringString";
            } else {
                return "MapStringObject";
            }
        }
        return "MapObjectObject";
    }
}

