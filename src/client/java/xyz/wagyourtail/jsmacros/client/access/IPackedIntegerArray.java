package xyz.wagyourtail.jsmacros.client.access;

public interface IPackedIntegerArray {

    long jsmacros_getMaxValue();

    int jsmacros_getElementsPerLong();

    int jsmacros_getIndexScale();

    int jsmacros_getIndexOffset();

    int jsmacros_getIndexShift();

}
