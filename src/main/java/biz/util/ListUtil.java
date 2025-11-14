package biz.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : 김언중
 * @since : 2023. 10. 09.
 * <p>
 * == 수정사항 ==
 * ---------------------------------------
 * 2023. 10. 09.  김언중 최초 생성
 */
public class ListUtil {

    public static boolean chkNull(List<?> list) {
        return list == null;
    }

    public static boolean isEmpty(List<?> list) {
        return chkNull(list) || list.isEmpty();
    }

    public static <R, P> R findEqual(List<R> list, String key, P val) {
        Optional<R> obj = list.stream()
                .findFirst()
                .filter(item -> {
                    try {
                        Field field = getFieldIfExists(item.getClass(), key);
                        if (field == null) {
                            return false;
                        }

                        field.setAccessible(true);
                        Object valField = field.get(item);

                        return valField != null && valField.equals(val);
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                });
        return obj.orElse(null);
    }

    public static <R, P> List<R> findEqualList(List<R> list, String key, P val) {
        return list.stream()
                .filter(item -> {
                    try {
                        Field field = getFieldIfExists(item.getClass(), key);
                        if (field == null) {
                            return false;
                        }

                        field.setAccessible(true);
                        Object valField = field.get(item);

                        return valField != null && valField.equals(val);
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public static <R, P extends Number> List<R> findGreaterList(List<R> list, String key, P val, boolean inclusive) {
        return list.stream()
                .filter(item -> {
                    try {
                        Field field = getFieldIfExists(item.getClass(), key);
                        if (field == null) {
                            return false;
                        }

                        if (!isNumericField(field)) {
                            return false;
                        }

                        field.setAccessible(true);
                        Object valField = field.get(item);

                        if (valField instanceof Number) {
                            double dblField = ((Number) valField).doubleValue();
                            double dblComparison = val.doubleValue();

                            return inclusive ? dblField >= dblComparison : dblField > dblComparison;
                        }
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static <R extends Number, P extends Number> List<R> findGreaterList(List<R> list, P val, boolean inclusive) {
        return list.stream()
                .filter(valNum -> {
                    double dblField = valNum.doubleValue();
                    double dblComparison = val.doubleValue();
                    return inclusive ? dblField >= dblComparison : dblField > dblComparison;
                })
                .collect(Collectors.toList());
    }

    public static <R, P extends Number> List<R> findLessList(List<R> list, String key, P val, boolean inclusive) {
        return list.stream()
                .filter(item -> {
                    try {
                        Field field = getFieldIfExists(item.getClass(), key);
                        if (field == null) {
                            return false;
                        }

                        if (!isNumericField(field)) {
                            return false;
                        }

                        field.setAccessible(true);
                        Object valField = field.get(item);

                        double dblField = ((Number) valField).doubleValue();
                        double dblComparison = val.doubleValue();

                        return inclusive ? dblField <= dblComparison : dblField < dblComparison;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public static <R extends Number, P extends Number> List<R> findLessList(List<R> list, P val, boolean inclusive) {
        return list.stream()
                .filter(valNum -> {
                    double dblField = valNum.doubleValue();
                    double dblComparison = val.doubleValue();

                    return inclusive ? dblField <= dblComparison : dblField < dblComparison;
                })
                .collect(Collectors.toList());
    }

    public static List<String> findContainStrList(List<String> list, String val) {
        if (StringUtil.chkNull(val)) {
            return list;
        }
        return list.stream()
                .filter(str -> str.contains(val))
                .collect(Collectors.toList());
    }

    public static <R> List<R> findContainStrList(List<R> list, String key, String val) {
        if (StringUtil.chkNull(val)) {
            return list;
        }
        return list.stream()
                .filter(item -> {
                    try {
                        Field field = getFieldIfExists(item.getClass(), key);
                        if (field == null) {
                            return false;
                        }

                        if (!isStringField(field)) {
                            return false;
                        }

                        field.setAccessible(true);
                        Object fieldValue = field.get(item);

                        return StringUtil.toStr(fieldValue).contains(val);
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private static Field getFieldIfExists(Class<?> cls, String key) {
        try {
            return cls.getDeclaredField(key);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static boolean isNumericField(Field field) {
        Class<?> fieldType = field.getType();
        return fieldType == int.class || fieldType == long.class || fieldType == short.class ||
                fieldType == double.class || fieldType == float.class ||
                fieldType == Integer.class || fieldType == Long.class ||
                fieldType == Short.class || fieldType == Double.class ||
                fieldType == Float.class;
    }

    private static boolean isStringField(Field field) {
        return field.getType() == String.class;
    }
}