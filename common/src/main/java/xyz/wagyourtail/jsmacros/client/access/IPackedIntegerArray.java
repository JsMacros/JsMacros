package xyz.wagyourtail.jsmacros.client.access;

public interface IPackedIntegerArray {

    long getMaxValue();
    int getElementsPerLong();
    int getIndexScale();
    int getIndexOffset();
    int getIndexShift();

}
