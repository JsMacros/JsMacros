package xyz.wagyourtail.jsmacros.client.api.classes.filter.compare;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;

import java.util.Locale;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class NumberCompareFilter implements IFilter<Number> {

    private final String operation;
    private final Number compareTo;
    private final String numberType;

    public NumberCompareFilter(String operation, Number compareTo, String numberType) {
        this.operation = operation;
        this.compareTo = compareTo;
        this.numberType = numberType;
    }

    @Override
    public Boolean apply(Number t) {
        return applyOperation(t);
    }

    // I know this is not ideal, but there is no easy way to compare dynamic numbers during runtime
    private boolean applyOperation(Number num) {
        switch (numberType.toLowerCase(Locale.ROOT)) {
            case "byte":
                return compareByte(num, compareTo);
            case "short":
                return compareShort(num, compareTo);
            case "int":
                return compareInt(num, compareTo);
            case "long":
                return compareLong(num, compareTo);
            case "float":
                return compareFloat(num, compareTo);
            case "double":
                return compareDouble(num, compareTo);
            default:
                throw new IllegalArgumentException("The type of the number doesn't exist");
        }
    }

    private boolean compareDouble(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.doubleValue() > compareTo.doubleValue();
            case ">=":
                return num.doubleValue() >= compareTo.doubleValue();
            case "<":
                return num.doubleValue() < compareTo.doubleValue();
            case "<=":
                return num.doubleValue() <= compareTo.doubleValue();
            case "==":
                return num.doubleValue() == compareTo.doubleValue();
            case "!=":
                return num.doubleValue() != compareTo.doubleValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

    private boolean compareFloat(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.floatValue() > compareTo.floatValue();
            case ">=":
                return num.floatValue() >= compareTo.floatValue();
            case "<":
                return num.floatValue() < compareTo.floatValue();
            case "<=":
                return num.floatValue() <= compareTo.floatValue();
            case "==":
                return num.floatValue() == compareTo.floatValue();
            case "!=":
                return num.floatValue() != compareTo.floatValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

    private boolean compareLong(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.longValue() > compareTo.longValue();
            case ">=":
                return num.longValue() >= compareTo.longValue();
            case "<":
                return num.longValue() < compareTo.longValue();
            case "<=":
                return num.longValue()<= compareTo.longValue();
            case "==":
                return num.longValue() == compareTo.longValue();
            case "!=":
                return num.longValue() != compareTo.longValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

    private boolean compareInt(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.intValue() > compareTo.intValue();
            case ">=":
                return num.intValue() >= compareTo.intValue();
            case "<":
                return num.intValue() < compareTo.intValue();
            case "<=":
                return num.intValue() <= compareTo.intValue();
            case "==":
                return num.intValue() == compareTo.intValue();
            case "!=":
                return num.intValue() != compareTo.intValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

    private boolean compareShort(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.shortValue() > compareTo.shortValue();
            case ">=":
                return num.shortValue() >= compareTo.shortValue();
            case "<":
                return num.shortValue() < compareTo.shortValue();
            case "<=":
                return num.shortValue() <= compareTo.shortValue();
            case "==":
                return num.shortValue() == compareTo.shortValue();
            case "!=":
                return num.shortValue() != compareTo.shortValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

    private boolean compareByte(Number num, Number compareTo) {
        switch (operation) {
            case ">":
                return num.byteValue() > compareTo.byteValue();
            case ">=":
                return num.byteValue() >= compareTo.byteValue();
            case "<":
                return num.byteValue() < compareTo.byteValue();
            case "<=":
                return num.byteValue() <= compareTo.byteValue();
            case "==":
                return num.byteValue() == compareTo.byteValue();
            case "!=":
                return num.byteValue() != compareTo.byteValue();
            default:
                throw new IllegalArgumentException("Unknown operation, try < > <= => == != instead of " + operation);
        }
    }

}
