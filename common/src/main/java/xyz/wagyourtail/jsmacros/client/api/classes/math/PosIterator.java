package xyz.wagyourtail.jsmacros.client.api.classes.math;

import java.util.Iterator;

/**
 * Will treat Vec2D's x2 as z1 and y2 as x2
 * @author MelonRind
 * @since 1.8.4
 */
public class PosIterator implements Iterator<Double> {
    public double x1;
    public double y1;
    public double z1;
    public double x2;
    public double y2;
    public double z2;
    private int index = 0;
    private int max;

    public PosIterator(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
        this.max = 2;
    }

    public PosIterator(double x1, double y1, double z1) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.max = 3;
    }

    public PosIterator(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = x2;
        this.x2 = y2;
        this.max = 4;
    }

    public PosIterator(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.max = 6;
    }

    @Override
    public boolean hasNext() {
        return index < max;
    }

    @Override
    public Double next() {
        switch (index++) {
            case 0:
                return x1;
            case 1:
                return y1;
            case 2:
                return z1;
            case 3:
                return x2;
            case 4:
                return y2;
            case 5:
                return z2;
        }
        return 0.0;
    }

}
