package xyz.wagyourtail.jsmacros.api.math;

import java.util.Objects;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
public class Vec2D {
    public double x1;
    public double y1;
    public double x2;
    public double y2;

    public Vec2D(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Vec2D(Pos2D start, Pos2D end) {
        this.x1 = start.x;
        this.y1 = start.y;
        this.x2 = end.x;
        this.y2 = end.y;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getDeltaX() {
        return x2 - x1;
    }

    public double getDeltaY() {
        return y2 - y1;
    }

    public Pos2D getStart() {
        return new Pos2D(x1, y1);
    }

    public Pos2D getEnd() {
        return new Pos2D(x2, y2);
    }

    public double getMagnitude() {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @return magnitude squared
     * @since 1.6.5
     */
    public double getMagnitudeSq() {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public Vec2D add(Vec2D vec) {
        return new Vec2D(x1 + vec.x1, y1 + vec.y1, x2 + vec.x2, y2 + vec.y2);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     * @since 1.6.3
     */
    public Vec2D add(double x1, double y1, double x2, double y2) {
        return new Vec2D(this.x1 + x1, this.y1 + y1, this.x2 + x2, this.y2 + y2);
    }

    public Vec2D multiply(Vec2D vec) {
        return new Vec2D(x1 * vec.x1, y1 * vec.y1, x2 * vec.x2, y2 * vec.y2);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     * @since 1.6.3
     */
    public Vec2D multiply(double x1, double y1, double x2, double y2) {
        return new Vec2D(this.x1 * x1, this.y1 * y1, this.x2 * x2, this.y2 * y2);
    }

    /**
     * @param scale
     * @return
     * @since 1.6.3
     */
    public Vec2D scale(double scale) {
        return new Vec2D(x1 * scale, y1 * scale, x2 * scale, y2 * scale);
    }

    public double dotProduct(Vec2D vec) {
        double dx1 = x2 - x1;
        double dx2 = vec.x2 - vec.x1;
        double dy1 = y2 - y1;
        double dy2 = vec.y2 - vec.y1;
        return dx1 * dx2 + dy1 * dy2;
    }

    public Vec2D reverse() {
        return new Vec2D(x2, y2, x1, y1);
    }

    /**
     * @return a new Vec2D with the same direction but a magnitude of 1
     * @since 1.6.5
     */
    public Vec2D normalize() {
        double mag = getMagnitude();
        return new Vec2D(x1 / mag, y1 / mag, x2 / mag, y2 / mag);
    }

    public String toString() {
        return String.format("%f, %f -> %f, %f", x1, y1, x2, y2);
    }

    public Vec3D to3D() {
        return new Vec3D(x1, y1, 0, x2, y2, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2D vec2D = (Vec2D) o;
        return Double.compare(x1, vec2D.x1) == 0
                && Double.compare(y1, vec2D.y1) == 0
                && Double.compare(x2, vec2D.x2) == 0
                && Double.compare(y2, vec2D.y2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2);
    }

    public int compareTo(Vec2D other) {
        int i = getStart().compareTo(other.getStart());
        if (i == 0) {
            i = getEnd().compareTo(other.getEnd());
        }
        return i;
    }

}
