package code.support.demo.net;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 请求参数 对象,Map<String, String> 映射
 */
public class RetrofitParam {

    /**
     * @param obj 请求的参数
     */
    public static Map<String, String> getParam(@NonNull Object obj) {

        Map<String, String> params = new IdentityHashMap<>();
        try {
            Class clazz = obj.getClass();
            // 自己的Field
            Field[] fields = clazz.getDeclaredFields();
            // 父类的Field
            Field[] superFields = clazz.getSuperclass().getDeclaredFields();

            boolean isDerivedFieldNull = (fields == null || fields.length == 0);
            boolean isSupperFieldNull = (superFields == null || superFields.length == 0);

            if (isDerivedFieldNull && isSupperFieldNull) {
                return Collections.emptyMap();
            }

            /*父类 字段名值对*/
            for (Field superField : superFields) {
                superField.setAccessible(true);
                // 得到字段值
                String fieldName = superField.getName();
                Object fieldValue = superField.get(obj);

                /*特殊字段处理*/
                if (fieldValue == null) {
                    fieldValue = "";
                } else if (fieldValue.equals(0)) {
                    fieldValue = "";
                }

                if ("clazz".equals(fieldName)) {
                    fieldName = "class";
                }
                params.put(fieldName, String.valueOf(fieldValue));
                Log.e("param_convert-super", fieldName + "---" + fieldValue);
            }


             /*子类自己 字段名值对*/
            for (Field field : fields) {
                // 得到字段名
                String fieldName = field.getName();
                field.setAccessible(true);
                // 得到字段值
                Object fieldValue = field.get(obj);

                /*特殊字段处理*/
                if (fieldValue == null) {
                    fieldValue = "";
                } else if (fieldValue.equals(0)) {
                    fieldValue = "";
                }

                if ("clazz".equals(fieldName)) {
                    fieldName = "class";
                }
                params.put(fieldName, String.valueOf(fieldValue));

                Log.e("param_convert", fieldName + "---" + fieldValue);
            }
            return params;

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}