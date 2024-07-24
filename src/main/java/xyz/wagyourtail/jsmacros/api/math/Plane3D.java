package xyz.wagyourtail.jsmacros.api.math;

import java.util.Objects;

/**
 * @since 1.6.5
 */
public class Plane3D {
    public double x1;
    public double y1;
    public double z1;
    public double x2;
    public double y2;
    public double z2;
    public double x3;
    public double y3;
    public double z3;

    public Plane3D(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.x3 = x3;
        this.y3 = y3;
        this.z3 = z3;
    }

    public Vec3D getNormalVector() {
        return new Vec3D(x1, y1, z1, x2, y2, z2).crossProduct(new Vec3D(x2, y2, z2, x3, y3, z3));
    }

    public Vec3D getVec12() {
        return new Vec3D(x1, y1, z1, x2, y2, z2);
    }

    public Vec3D getVec13() {
        return new Vec3D(x1, y1, z1, x3, y3, z3);
    }

    public Vec3D getVec23() {
        return new Vec3D(x2, y2, z2, x3, y3, z3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plane3D plane3D = (Plane3D) o;
        return Double.compare(x1, plane3D.x1) == 0
                && Double.compare(y1, plane3D.y1) == 0
                && Double.compare(z1, plane3D.z1) == 0
                && Double.compare(x2, plane3D.x2) == 0
                && Double.compare(y2, plane3D.y2) == 0
                && Double.compare(z2, plane3D.z2) == 0
                && Double.compare(x3, plane3D.x3) == 0
                && Double.compare(y3, plane3D.y3) == 0
                && Double.compare(z3, plane3D.z3) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

}
