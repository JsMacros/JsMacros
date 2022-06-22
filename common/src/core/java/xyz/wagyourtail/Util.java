package xyz.wagyourtail;

public class Util {

    public static Object tryAutoCastNumber(Class<?> returnType, Object number) {
        if ((returnType == int.class || returnType == Integer.class) && !(number instanceof Integer)) {
            number = ((Number) number).intValue();
        } else if ((returnType == float.class || returnType == Float.class) && !(number instanceof Float)) {
            number = ((Number) number).floatValue();
        } else if ((returnType == double.class || returnType == Double.class) && !(number instanceof Double)) {
            number = ((Number) number).doubleValue();
        } else if ((returnType == short.class || returnType == Short.class) && !(number instanceof Short)) {
            number = ((Number) number).shortValue();
        } else if ((returnType == long.class || returnType == Long.class) && !(number instanceof Long)) {
            number = ((Number) number).longValue();
        } else if ((returnType == char.class || returnType == Character.class) && !(number instanceof Character)) {
            number = (char) ((Number) number).intValue();
        } else if ((returnType == byte.class || returnType == Byte.class) && !(number instanceof Byte)) {
            number = ((Number) number).byteValue();
        }
        return number;
    }

}
