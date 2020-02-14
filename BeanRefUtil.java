package com.hw.dp.dsp.dedicated.shanxi.mask.utils;

import com.hw.dp.dsp.dedicated.shanxi.mask.annotation.KeyMapping;
import com.hw.dp.dsp.dedicated.shanxi.mask.utils.parent.IBeanParent;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LYL-PC
 * @所属项目 DP_DataParser_Common_SWP_SanDong_GPS_DATA
 * @类名称 BeanRefUtil
 * @类作用 反射工具类
 * @类作者 LYL-PC
 * @创建日期 2019/12/31
 * @审核人
 * @审核日期
 * @更新记录
 * @其它备注
 */
public class BeanRefUtil {
    private static final char UNDERLINED = '_';
    public static final String STRING = "String";
    public static final String INTEGER = "Integer";
    public static final String INT = "int";
    public static final String LONG = "Long";
    public static final String FLOAT = "Float";
    public static final String DOUBLE = "Double";
    public static final String BOOLEAN = "Boolean";
    public static final String DATE = "Date";

    /**
     * 通过反射 调用set方法，sourceObj  ====》 Entity
     *      借用了自定义注解 KeyMapping
     * @param clazz Entity类型
     * @param beanParent 属性对象
     * @return
     * @throws Exception
     */
    public static <T> T toEntity(Class<T> clazz, IBeanParent beanParent) throws Exception {
        Object bean = clazz.newInstance();
        // 取出bean里的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String simpleName = type.getSimpleName();

            String fieldName = field.getName();
            String fieldSetName = BeanRefUtil.parSetName(fieldName);
            if (!BeanRefUtil.checkSetMet(methods, fieldSetName)) {
                continue;
            }
            Method fieldSetMet = clazz.getMethod(fieldSetName, type);
            fieldSetMet.setAccessible(true);
            //KeyMapping是自定义注解
            KeyMapping annotation = field.getAnnotation(KeyMapping.class);
            if(annotation == null) {
                annotation = fieldSetMet.getAnnotation(KeyMapping.class);
            }

            String name = fieldName;
            if(annotation != null) {
                name = annotation.name();
            }

            Object rowValue = beanParent.get(name);
            Object value = BeanRefUtil.parse(simpleName, rowValue);
            fieldSetMet.invoke(bean, value);
        }
        return (T) bean;
    }

    /**
     * 取Bean的属性和值对应关系的MAP
     *
     * @param bean
     * @return Map
     */
    public static Map<String, Object> getFieldValueMap(Object bean) throws Exception {
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        Map<String, Object> fieldValMap = new HashMap<String, Object>();
        for (Field field : fields) {
            String name = field.getName();
            String type = field.getType().getSimpleName();
            String fieldGetName = parGetName(name);
            if (!checkGetMet(methods, fieldGetName)) {
                continue;
            }
            Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
            Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
            fieldValMap.put(name, fieldVal);
        }
        return fieldValMap;
    }

    /**
     * 反射set方法
     *
     * @param clazz       目标对象class类型
     * @param paramsArray key数组
     * @param valueArray  value数组
     * @return
     * @throws Exception
     */
    public static Object setFieldValue(Class clazz, String[] paramsArray, String[] valueArray) throws Exception {
        Map<String, String> valMap = new HashMap<String, String>(16);
        int paramLength = paramsArray.length;
        int valueLength = valueArray.length;
        //取最小的 length
        int length = paramLength > valueLength ? valueLength : paramLength;
        for (int i = length; i-- > 0; ) {
            String param = paramsArray[i];
            String value = valueArray[i];
            valMap.put(param, value);
        }
        //return setFieldValue(clazz, valMap);
        Object bean = clazz.newInstance();
        BeanUtils.populate(bean, valMap);
        return bean;
    }

    /**
     * 反射set方法
     *
     * @param clazz  目标对象class类型
     * @param valMap key-value
     * @return
     * @throws Exception
     */
    public static Object setFieldValue(Class clazz, Map<String, String> valMap) throws Exception {
        Object bean = clazz.newInstance();
        // 取出bean里的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fieldSetName = parSetName(field.getName());
            if (!checkSetMet(methods, fieldSetName)) {
                continue;
            }
            Method fieldSetMet = clazz.getMethod(fieldSetName,
                    field.getType());
            String fieldKeyName = field.getName();
            String value = valMap.get(fieldKeyName);
            if (StringUtils.isNotBlank(value)) {
                String fieldType = field.getType().getSimpleName();
                Object objectValue = parse(fieldType, value);
                fieldSetMet.invoke(bean, objectValue);
            }
        }
        return bean;
    }

    /**
     * 复制bean的部分属性值
     *
     * @param bean       目标bean
     * @param paramNames 成员属性名数组， 可选，如果不传，则全部复制
     * @param <T>        bean类型
     * @return
     */
    public static <T> T copyPartParamValueFromBean(T bean, String... paramNames) throws Exception {
        Class<?> clazz = bean.getClass();
        Object newBean = clazz.newInstance();

        // 取出bean里的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        int length = paramNames.length;
        for (Field field : fields) {
            String fieldName = field.getName();
            if (length <= 0 || need2Copy(fieldName, paramNames)) {
                String fieldGetName = parGetName(fieldName);
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                String fieldSetName = parSetName(fieldName);
                if (!checkSetMet(methods, fieldSetName)) {
                    continue;
                }
                //取出数据
                Method fieldGetMet = clazz.getMethod(fieldGetName, new Class[]{});
                fieldGetMet.setAccessible(true);
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                //复制到新的bean中
                if (fieldVal != null) {
                    String fieldType = field.getType().getSimpleName();
                    Object objectValue = parse(fieldType, fieldVal);
                    Method fieldSetMet = clazz.getMethod(fieldSetName, field.getType());
                    fieldSetMet.setAccessible(true);
                    fieldSetMet.invoke(newBean, objectValue);
                }
            }
        }
        return (T) newBean;
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == UNDERLINED) {
            startIndex = 1;
        }
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == UNDERLINED) {
            startIndex = 1;
        }
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在某属性的 set方法
     *
     * @param methods
     * @param fieldSetMet
     * @return boolean
     */
    public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 类型转换
     *
     * @param fieldType 类型
     * @param value     目标值
     * @return
     * @throws Exception
     */
    public static Object parse(String fieldType, Object value) throws Exception {
        String val = String.valueOf(value);
        if (STRING.equals(fieldType)) {
            return value;
        } else if (INTEGER.equals(fieldType) || INT.equals(fieldType)) {
            Integer intValue = Integer.parseInt(val);
            return intValue;
        } else if (LONG.equalsIgnoreCase(fieldType)) {
            Long longValue = Long.parseLong(val);
            return longValue;
        } else if (FLOAT.equalsIgnoreCase(fieldType)) {
            Float floatValue = Float.parseFloat(val);
            return floatValue;
        } else if (DOUBLE.equalsIgnoreCase(fieldType)) {
            Double doubleValue = Double.parseDouble(val);
            return doubleValue;
        } else if (BOOLEAN.equalsIgnoreCase(fieldType)) {
            Boolean booleanValue = Boolean.parseBoolean(val);
            return booleanValue;
        } else if (DATE.equals(fieldType)) {
            Date date = parseDate(val);
            return date;
        } else {
            throw new Exception("not supper type" + fieldType);
        }
    }

    public static java.sql.Date parseDate(String dateStr) throws Exception {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        String fmtStr = null;
        if (dateStr.indexOf(':') > 0) {
            fmtStr = "yyyy-MM-dd HH:mm:ss";
        } else {
            fmtStr = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(fmtStr);
        Date date = sdf.parse(dateStr);
        //转为 java.sql.date
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return sqlDate;
    }

    /**
     * 判断是否需要复制
     *
     * @param fieldName  当前成员属性名
     * @param paramNames 需要复制的成员名
     * @return
     */
    private static boolean need2Copy(String fieldName, String... paramNames) {
        for (String paramName : paramNames) {
            if (fieldName.equals(paramName)) {
                return true;
            }
        }
        return false;
    }
}
